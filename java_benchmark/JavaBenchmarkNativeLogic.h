#ifndef _JAVA_BENCHMARK_NATIVE_H
#define _JAVA_BENCHMARK_NATIVE_H

#ifdef __c
	extern "C" {
#endif
		long threadMigration();

#ifdef __c
	}
#endif

#endif
