#include "C_Logic.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

char python_plot_location[] = "/home/cristi/Desktop/scs_project/python_plot";
char thread_migration[] = "/c/thread_migration/threadMigration.txt";
char memory_allocation_malloc[] = "/c/mem_allocation/memoryAllocation_MALLOC.txt";
char memory_allocation_calloc[] = "/c/mem_allocation/memoryAllocation_CALLOC.txt";
char memory_deallocation_malloc[] = "/c/mem_deallocation/memoryDeallocation_MALLOC.txt";
char memory_deallocation_calloc[] = "/c/mem_deallocation/memoryDeallocation_CALLOC.txt";
char memory_access[] = "/c/mem_access/memoryAccess.txt";
char thread_creation[] = "/c/thread_creation/threadCreation.txt";
char thread_switch_context_suspend[] = "/c/thread_switch_context/threadSwitchContext_SUSPEND.txt";
char thread_switch_context_resume[] = "/c/thread_switch_context/threadSwitchContext_RESUME.txt";

void printToFile(long* values, int size, char* fileName) {
	FILE *filePointer;

	//creates the file if it does not exist
	//if it exists, deletes content
	filePointer = fopen(fileName, "w");
	if(filePointer == NULL) {
		printf("Can not open file with name %s\n", fileName);
		exit(1);
	}
	for(int i =0;i<size; i++) {
		fprintf(filePointer, "%ld\n", values[i]);
	}
	fclose(filePointer);
}

void computeThreadMigrationElapsedTime(int nrTriesThreadMigration) {
	long* values = (long*) calloc(nrTriesThreadMigration, sizeof(long));


	for(int i =0;i<nrTriesThreadMigration;i++) {
		values[i] = threadMigration();
	}

	char pythonPlotCopy[100];
	strcpy(pythonPlotCopy, python_plot_location);
	printToFile(values, nrTriesThreadMigration, strcat(pythonPlotCopy, thread_migration));

	free(values);
}

void computeMemoryAllocationAndDeallocationElapsedTime(int nrTriesMemoryAllocation, int sizeMemoryAllocation) {
	long* valuesMalloc = (long*) calloc(nrTriesMemoryAllocation, sizeof(long));
	long* valuesCalloc = (long*) calloc(nrTriesMemoryAllocation, sizeof(long));
	long* memoryDeallocationFromMalloc = (long*) calloc(nrTriesMemoryAllocation, sizeof(long));
	long* memoryDeallocationFromCalloc = (long*) calloc(nrTriesMemoryAllocation, sizeof(long));

	long* result;
	for(int i =0;i<nrTriesMemoryAllocation;i++) {
		//1 sent as parameter means we use malloc
		result = memoryAllocationAndDeallocation(sizeMemoryAllocation, 1);
		valuesMalloc[i] = result[0];
		memoryDeallocationFromMalloc[i] = result[1];
		result = memoryAllocationAndDeallocation(sizeMemoryAllocation, 0);
		valuesCalloc[i] = result[0];
		memoryDeallocationFromCalloc[i] = result[1];
		free(result);
	}

	char pythonPlotCopy[100];
	strcpy(pythonPlotCopy, python_plot_location);
	printToFile(valuesMalloc, nrTriesMemoryAllocation, strcat(pythonPlotCopy, memory_allocation_malloc));
	
	strcpy(pythonPlotCopy, python_plot_location);
	printToFile(valuesCalloc, nrTriesMemoryAllocation, strcat(pythonPlotCopy, memory_allocation_calloc));

	strcpy(pythonPlotCopy, python_plot_location);
	printToFile(memoryDeallocationFromMalloc, nrTriesMemoryAllocation, strcat(pythonPlotCopy, memory_deallocation_malloc));

	strcpy(pythonPlotCopy, python_plot_location);
	printToFile(memoryDeallocationFromCalloc, nrTriesMemoryAllocation, strcat(pythonPlotCopy, memory_deallocation_calloc));

	free(valuesMalloc);
	free(valuesCalloc);
	free(memoryDeallocationFromMalloc);
	free(memoryDeallocationFromCalloc);
}

void computeMemoryAccessElapsedTime(int nrTriesMemoryAccess) {
	long *values = (long*) calloc(nrTriesMemoryAccess, sizeof(long));

	for(int i =0;i<nrTriesMemoryAccess;i++) {
		values[i] = memoryAccess();
	}

	char pythonPlotCopy[100];
	strcpy(pythonPlotCopy, python_plot_location);
	printToFile(values, nrTriesMemoryAccess, strcat(pythonPlotCopy, memory_access));

	free(values);
}

void computeThreadCreationTime(int nrTriesThreadCreation) {
	long *values = (long*) calloc(nrTriesThreadCreation, sizeof(long));

	for(int i =0;i<nrTriesThreadCreation;i++) {
		values[i] = threadCreation();
	}

	char pythonPlotCopy[100];
	strcpy(pythonPlotCopy, python_plot_location);
	printToFile(values, nrTriesThreadCreation, strcat(pythonPlotCopy, thread_creation));

	free(values);
}

void computeThreadSwitchContextElapsedTime(int nrTriesThreadSwitchContext) {
	long *suspendValues = (long*) calloc(nrTriesThreadSwitchContext, sizeof(long));
	long *resumeValues = (long*) calloc(nrTriesThreadSwitchContext, sizeof(long));

	long suspend, resume;

	for(int i =0;i<nrTriesThreadSwitchContext;i++) {
		threadSwitchContext(&suspend, &resume);
		suspendValues[i] = suspend;
		resumeValues[i] = resume;
	}

	char pythonPlotCopy[100];
	strcpy(pythonPlotCopy, python_plot_location);
	printToFile(suspendValues, nrTriesThreadSwitchContext, strcat(pythonPlotCopy, thread_switch_context_suspend));
	
	strcpy(pythonPlotCopy, python_plot_location);
	printToFile(resumeValues, nrTriesThreadSwitchContext, strcat(pythonPlotCopy, thread_switch_context_resume));

	free(resumeValues);
	free(suspendValues);
}

void main(int argc, char* argv[]) {
	if(argc!=7) {
		printf("Six arguments are required! \n");
		exit(1);
	}

	int nrTriesMemoryAllocation = atoi(argv[1]);
	int sizeMemoryAllocation = atoi(argv[2]);
	int nrTriesMemoryAccess = atoi(argv[3]);
	int nrTriesThreadCreation = atoi(argv[4]);
	int nrTriesThreadSwitchContext = atoi(argv[5]);
	int nrTriesThreadMigration = atoi(argv[6]);

	srand(time(NULL));

	computeMemoryAllocationAndDeallocationElapsedTime(nrTriesMemoryAllocation, sizeMemoryAllocation);
	computeMemoryAccessElapsedTime(nrTriesMemoryAccess);
	computeThreadCreationTime(nrTriesThreadCreation);
	computeThreadSwitchContextElapsedTime(nrTriesThreadSwitchContext);
	computeThreadMigrationElapsedTime(nrTriesThreadMigration);

	//call python plot on language c
	system("/home/cristi/Desktop/scs_project/python_plot/python_plot.py c");
	
}
