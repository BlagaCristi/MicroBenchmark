#ifndef C_LOGIC_H
#define C_LOGIC_H

#ifdef __c
	extern "C" {
#endif
		long threadMigration();
		long* memoryAllocationAndDeallocation(int, int);
		long memoryAccess();
		long threadCreation();
		void threadSwitchContext(long*, long*);

#ifdef __c
	}
#endif

#endif