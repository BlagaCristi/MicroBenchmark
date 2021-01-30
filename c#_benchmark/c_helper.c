#define _GNU_SOURCE

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h> 
#include <pthread.h>
#include <time.h>
#include <sched.h>

pthread_mutex_t lock;
long elapsedTime;

int getRandomNumber(int lowerLimit, int upperLimit) {
	return rand() % (upperLimit - lowerLimit + 1) + lowerLimit; 
}

void* threadFuncion(void *arg) {

	int currentCpuCore;
	int numberOfCoresOnline;
	cpu_set_t cpuMask;
	cpu_set_t newCpuMask;
	int newCpuCore;
	struct timespec startTime, finishTime;
	
	//get lock
	pthread_mutex_lock(&lock);

	//Get the core on which the thread is currently running
	currentCpuCore = sched_getcpu();

	//Get number of cores online
	numberOfCoresOnline = sysconf(_SC_NPROCESSORS_ONLN);

	//find a core that is online and different than the current one
	newCpuCore = getRandomNumber(0, numberOfCoresOnline - 1);
	while(newCpuCore == currentCpuCore) {
		newCpuCore = getRandomNumber(0, numberOfCoresOnline - 1);
	}

	//initialize mask with 0
	CPU_ZERO(&newCpuMask);

	//set newCpuCore bit in mask to 1
	CPU_SET(newCpuCore, &newCpuMask);

	if(clock_gettime(CLOCK_MONOTONIC, &startTime) == -1) {
		exit(2);
	}

	if(sched_setaffinity(0, sizeof(cpu_set_t), &newCpuMask) == 1) {
		exit(3);
	}
	if(clock_gettime(CLOCK_MONOTONIC, &finishTime) == -1) {
		exit(4);
	}

	currentCpuCore = sched_getcpu();
	if(currentCpuCore != newCpuCore) {
		exit(5);
	} 

	//compute elapsed time in nanoseconds
	elapsedTime = finishTime.tv_nsec - startTime.tv_nsec;
	
	//release lock
	pthread_mutex_unlock(&lock);
}

long threadMigration() {
	pthread_t thread_id;
	int result;

	//use current time as seed for random generator
	srand(time(0));

	//create a lock to share with the thread
	result = pthread_mutex_init(&lock, NULL);
	if(result != 0) {
		exit(6);
	}

	//get the lock before creating the thread to ensure the thread does not execute
	//its code before we pause the main process
	pthread_mutex_lock(&lock);

	//create thread
	result = pthread_create(&thread_id, NULL, threadFuncion, NULL);
	if(result != 0) {
		exit(7);
	}

	//release lock for thread to execute
	pthread_mutex_unlock(&lock);

	//wait for thread to finish execution
	pthread_join(thread_id, NULL);

	//destroy lock
	pthread_mutex_destroy(&lock);

	return elapsedTime;
}
