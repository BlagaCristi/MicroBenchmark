#define _GNU_SOURCE

#include "C_Logic.h"
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h> 
#include <pthread.h>
#include <time.h>
#include <sched.h>

pthread_mutex_t migrationlock, threadSwitchContextLock;
pthread_cond_t threadSwitchContextConditionVariable;

struct timespec threadSwitchContextStartSuspend, threadSwitchContextFinishSuspend, threadSwitchContextStartResume, threadSwitchContextFinishResume;
int wakeUpSwitch;

long migrationElapsedTime;

int getRandomNumber(int lowerLimit, int upperLimit) {
	return rand() % (upperLimit - lowerLimit + 1) + lowerLimit; 
}


/*THREAD MIGRATION*/

void* migrationThreadFunction(void *arg) {

	int currentCpuCore;
	int numberOfCoresOnline;
	cpu_set_t cpuMask;
	cpu_set_t newCpuMask;
	int newCpuCore;
	struct timespec startTime, finishTime;
	
	//get lock
	pthread_mutex_lock(&migrationlock);

	//Get the core on which the thread is currently running
	currentCpuCore = sched_getcpu();
	printf("Thread running on core = %d\n", currentCpuCore);

	//Get number of cores online
	numberOfCoresOnline = sysconf(_SC_NPROCESSORS_ONLN);
	printf("Number of cores online = %d\n", numberOfCoresOnline);

	//initial affinity - cores on which the thread can run
	if(sched_getaffinity(0, sizeof(cpu_set_t), &cpuMask) == -1) {
		printf("Can not get scheduler affinity\n");
		exit(1);
	}
	printf("Cores on which the thread can run: \n");
	for(int i = 0; i< numberOfCoresOnline; i++) {
		//return 1 if core i is in mask or 0 otherwise
		printf("%d ", CPU_ISSET(i, &cpuMask));
	}
	printf("\n");

	//find a core that is online and different than the current one
	newCpuCore = getRandomNumber(0, numberOfCoresOnline - 1);
	while(newCpuCore == currentCpuCore) {
		newCpuCore = getRandomNumber(0, numberOfCoresOnline - 1);
	}

	printf("Core on which the thread will be moved: %d\n", newCpuCore);

	//initialize mask with 0
	CPU_ZERO(&newCpuMask);

	//set newCpuCore bit in mask to 1
	CPU_SET(newCpuCore, &newCpuMask);

	printf("1st check\n");
	if(clock_gettime(CLOCK_MONOTONIC, &startTime) == -1) {
		printf("Error getting clock time\n");
		exit(2);
	}

	if(sched_setaffinity(0, sizeof(cpu_set_t), &newCpuMask) == 1) {
		printf("Can not set scheduler affinity\n");
		exit(3);
	}

	if(clock_gettime(CLOCK_MONOTONIC, &finishTime) == -1) {
		printf("Error getting clock time\n");
		exit(4);
	}
	printf("4th check\n");

	currentCpuCore = sched_getcpu();
	if(currentCpuCore == newCpuCore) {
		printf("SUCCESS! Thread now running on core: %d\n", currentCpuCore);
	} else {
		printf("FAILED :(\n");
		exit(5);
	}

	migrationElapsedTime = finishTime.tv_nsec - startTime.tv_nsec;
	printf("Time taken in nanoseconds: %ld\n", migrationElapsedTime);

	//release lock
	pthread_mutex_unlock(&migrationlock);
}

long threadMigration() {

	pthread_t thread_id;
	int result;

	//use current time as seed for random generator

	//create a lock to share with the thread
	result = pthread_mutex_init(&migrationlock, NULL);
	if(result != 0) {
		printf("Can not initialize mutex\n");
		exit(6);
	}

	//get the lock before creating the thread to ensure the thread does not execute
	//its code before we pause the main process
	pthread_mutex_lock(&migrationlock);

	//create thread
	result = pthread_create(&thread_id, NULL, migrationThreadFunction, NULL);
	if(result != 0) {
		printf("Thread can not be created\n");
		exit(7);
	}

	//release lock for thread to execute
	pthread_mutex_unlock(&migrationlock);

	//wait for thread to finish execution
	pthread_join(thread_id, NULL);

	//destroy lock
	pthread_mutex_destroy(&migrationlock);

	return migrationElapsedTime;
}

/*MEMORY ALLOCATION*/

long* memoryAllocationAndDeallocation(int size, int isMalloc) {
	struct timespec startTime, finishTime;
	long* result = (long*)calloc(2, sizeof(long));

	if(clock_gettime(CLOCK_MONOTONIC, &startTime) == -1) {
		printf("Error getting clock time\n");
		exit(2);
	}

	//memory allocation
	int *values;
	if(isMalloc) {
		values = (int*) malloc(size * sizeof(int));
	} else {
		values = (int*) calloc(size, sizeof(int));
	}

	if(values == NULL) {
		printf("Failed to allocate memory\n");
		exit(2);
	}

	if(clock_gettime(CLOCK_MONOTONIC, &finishTime) == -1) {
		printf("Error getting clock time\n");
		exit(2);
	}

	result[0] =  finishTime.tv_nsec - startTime.tv_nsec;
	
	//memory deallocation
	if(clock_gettime(CLOCK_MONOTONIC, &startTime) == -1) {
		printf("Error getting clock time\n");
		exit(2);
	}
	free(values);
	if(clock_gettime(CLOCK_MONOTONIC, &finishTime) == -1) {
		printf("Error getting clock time\n");
		exit(2);
	}
	result[1] = finishTime.tv_nsec - startTime.tv_nsec;
	return result;

}

/*MEMORY ACCESS*/

long memoryAccess() {
	long* memValues = (long*)malloc(100 * sizeof(long));

	int elemPos = getRandomNumber(0, 100);

	struct timespec startTime, finishTime;

	if(clock_gettime(CLOCK_MONOTONIC, &startTime) == -1) {
		printf("Error getting clock time\n");
		exit(2);
	}

	int val = *(memValues + elemPos);

	if(clock_gettime(CLOCK_MONOTONIC, &finishTime) == -1) {
		printf("Error getting clock time\n");
		exit(2);
	}

	free(memValues);

	return finishTime.tv_nsec - startTime.tv_nsec;
}

/*THREAD CREATION*/

void* threadCreationFunction(void* arg) {
	
}

long threadCreation() {
	pthread_t thread_id;
	int result;
	struct timespec startTime, finishTime;

	if(clock_gettime(CLOCK_MONOTONIC, &startTime) == -1) {
		printf("Error getting clock time\n");
		exit(2);
	}

	//create thread
	result = pthread_create(&thread_id, NULL, threadCreationFunction, NULL);
	if(result != 0) {
		printf("Thread can not be created\n");
		exit(7);
	}

	//wait for thread to finish execution
	pthread_join(thread_id, NULL);

	if(clock_gettime(CLOCK_MONOTONIC, &finishTime) == -1) {
		printf("Error getting clock time\n");
		exit(2);
	}

	return finishTime.tv_nsec - startTime.tv_nsec;
}

/*THREAD SWITCH CONTEXT*/

void* threadSwitchContextFunction(void *arg) {

	pthread_mutex_lock(&threadSwitchContextLock);

	if(clock_gettime(CLOCK_MONOTONIC, &threadSwitchContextStartSuspend) == -1) {
		printf("Error getting clock time\n");
		exit(2);
	}

	wakeUpSwitch = 0;

	//wake up main thread
	//thread SUSPENDED
	pthread_cond_signal(&threadSwitchContextConditionVariable);
	

	while(wakeUpSwitch == 0) {
		pthread_cond_wait(&threadSwitchContextConditionVariable, &threadSwitchContextLock);
	}

	//thread RESUMED

	if(clock_gettime(CLOCK_MONOTONIC, &threadSwitchContextFinishResume) == -1) {
		printf("Error getting clock time\n");
		exit(2);
	}

	pthread_mutex_unlock(&threadSwitchContextLock);

}

void threadSwitchContext(long* suspend, long* resume) {
	pthread_t thread_id;
	int result;

	//create a lock to share with the thread
	result = pthread_mutex_init(&threadSwitchContextLock, NULL);
	if(result != 0) {
		printf("Can not initialize mutex\n");
		exit(6);
	}

	//create a condition variable
	result = pthread_cond_init(&threadSwitchContextConditionVariable, NULL);
	if(result != 0) {
		printf("Can not initialize condition varialb\n");
		exit(6);
	}

	//get the lock 
	pthread_mutex_lock(&threadSwitchContextLock);
	wakeUpSwitch = 1;

	//create thread
	result = pthread_create(&thread_id, NULL, threadSwitchContextFunction, NULL);
	if(result != 0) {
		printf("Thread can not be created\n");
		exit(7);
	}

	while(wakeUpSwitch) {
		//wait for thread signal
		pthread_cond_wait(&threadSwitchContextConditionVariable, &threadSwitchContextLock);
	}

	if(clock_gettime(CLOCK_MONOTONIC, &threadSwitchContextFinishSuspend) == -1) {
		printf("Error getting clock time\n");
		exit(2);
	}

	if(clock_gettime(CLOCK_MONOTONIC, &threadSwitchContextStartResume) == -1) {
		printf("Error getting clock time\n");
		exit(2);
	}
	
	wakeUpSwitch = 1;

	pthread_cond_signal(&threadSwitchContextConditionVariable);

	//release lock
	pthread_mutex_unlock(&threadSwitchContextLock);

	//wait for thread to finish execution
	pthread_join(thread_id, NULL);

	//destroy lock
	pthread_mutex_destroy(&threadSwitchContextLock);

	//destroy condition variable
	pthread_cond_destroy(&threadSwitchContextConditionVariable);

	*suspend = threadSwitchContextFinishSuspend.tv_nsec - threadSwitchContextStartSuspend.tv_nsec;
	*resume = threadSwitchContextFinishResume.tv_nsec - threadSwitchContextStartResume.tv_nsec;
}
