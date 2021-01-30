using System;
using System.Threading; 

class BenchmarkLogic {

	private volatile Boolean isThreadRunning;

	public long[] ElapsedTimeMemoryAllocationAndDeallocation(int sizeMemoryAllocation, Boolean gcOn) {
		long memoryAllocation, memoryDeallocation;
		long frequency = System.Diagnostics.Stopwatch.Frequency; //the frequency at which the ticks are computed at

		//memory allocation
		System.Diagnostics.Stopwatch watch = new System.Diagnostics.Stopwatch();
		watch.Start ();
		int[] dummyArray = new int[sizeMemoryAllocation];
		watch.Stop ();
		memoryAllocation = watch.ElapsedTicks * 1000000000 / frequency;

		//memory deallocation
		watch = new System.Diagnostics.Stopwatch();
		watch.Start ();
		dummyArray = null;
		if(gcOn) {
			System.GC.Collect(); // force garbace collector to run
		}
		watch.Stop();
		memoryDeallocation = watch.ElapsedTicks * 1000000000 / frequency;
		long[] result = new long[2];
		result [0] = memoryAllocation;
		result [1] = memoryDeallocation;
		return result;
	}

	public long ElapsedTimeMemoryAccess() {
		long[] dummyArray = new long[100];
		long dummyValue;

		Random random = new Random();
		int randomNumber = random.Next(0, 100);
		long frequency = System.Diagnostics.Stopwatch.Frequency; //the frequency at which the ticks are computed at

		System.Diagnostics.Stopwatch watch = new System.Diagnostics.Stopwatch();
		watch.Start ();

		dummyValue = dummyArray [randomNumber];

		watch.Stop ();

		return watch.ElapsedTicks * 1000000000 / frequency;
	}

	private void DummyThreadFunction() {
		/* EMPTY */
	}

	public long ElapsedTimeThreadCreation() {
		long frequency = System.Diagnostics.Stopwatch.Frequency; //the frequency at which the ticks are computed at

		System.Diagnostics.Stopwatch watch = new System.Diagnostics.Stopwatch();
		watch.Start ();

		Thread thread = new Thread(new ThreadStart(this.DummyThreadFunction));  
		thread.Start ();
		thread.Join ();

		watch.Stop ();

		return watch.ElapsedTicks * 1000000000 / frequency;
	}

	private void ThreadSwitchContextFunction() {
		while (isThreadRunning) {
			/* EMPTY */
		}
	}

	public long[] ElapsedTimeThreadSwitchContext() {
		isThreadRunning = true;
		long[] result = new long[2];

		long frequency = System.Diagnostics.Stopwatch.Frequency; //the frequency at which the ticks are computed at
		System.Diagnostics.Stopwatch watch = new System.Diagnostics.Stopwatch();

		Thread thread = new Thread(new ThreadStart(this.ThreadSwitchContextFunction));  
		thread.Start ();

		// suspend thread
		watch.Start ();
		thread.Suspend ();
		watch.Stop ();

		result[0] = watch.ElapsedTicks * 1000000000 / frequency;

		watch = new System.Diagnostics.Stopwatch();
		watch.Start ();
		thread.Resume ();
		watch.Stop ();

		result[1] = watch.ElapsedTicks * 1000000000 / frequency;

		isThreadRunning = false;
		thread.Join ();

		return result;
	}
}