using System;
using System.Diagnostics;
using System.Runtime.InteropServices;

class App {

	static string PYTHON_PLOT = "/home/cristi/Desktop/scs_project/python_plot";
	static string MEMORY_ALLOCATION = PYTHON_PLOT + "/c#/mem_allocation/memAllocation.txt";
	static string MEMORY_ALLOCATION_GC_OFF = PYTHON_PLOT + "/c#/mem_deallocation/memDeallocation_gcOff.txt";
	static string MEMORY_ALLOCATION_GC_ON = PYTHON_PLOT + "/c#/mem_deallocation/memDeallocation_gcOn.txt";
	static string MEMORY_ACCESS = PYTHON_PLOT + "/c#/mem_access/memAccess.txt";
	static string THREAD_CREATION = PYTHON_PLOT + "/c#/thread_creation/threadCreation.txt";
	static string THREAD_SWITCH_CONTEXT_SUSPEND = PYTHON_PLOT + "/c#/thread_switch_context/threadSwitchContext_SUSPEND.txt";
	static string THREAD_SWITCH_CONTEXT_RESUME = PYTHON_PLOT + "/c#/thread_switch_context/threadSwitchContext_RESUME.txt";
	static string THREAD_MIGRATION = PYTHON_PLOT + "/c#/thread_migration/threadMigration.txt";

	static BenchmarkLogic benchmarkLogic;

	[DllImport("Chelper.so", EntryPoint="threadMigration")]
	static extern long threadMigration();

	static void Main(string[] args) {
		benchmarkLogic = new BenchmarkLogic();

		if(args.Length != 6) {
			Console.WriteLine("Six arguments are required!");
			Environment.Exit(1);
		}

		int nrTriesMemoryAllocation = Int32.Parse(args[0]);
		int sizeMemoryAllocation = Int32.Parse(args[1]);
		int nrTriesMemoryAccess = Int32.Parse(args[2]);
		int nrTriesThreadCreation = Int32.Parse(args[3]);
		int nrTriesThreadSwitchContext = Int32.Parse(args[4]);
		int nrTriesThreadMigration = Int32.Parse(args[5]);

		ComputeElapsedTimeMemoryAllocation (nrTriesMemoryAllocation, sizeMemoryAllocation, false);
		ComputeElapsedTimeMemoryAllocation (nrTriesMemoryAllocation, sizeMemoryAllocation, true);
		ComputeElapsedTimeMemoryAccess (nrTriesMemoryAccess);
		ComputeElapsedTimeThreadCreation (nrTriesThreadCreation);
		ComputeElapsedTimeThreadSwitchContext (nrTriesThreadSwitchContext);
		ComputeElapsedTimeThreadMigration (nrTriesThreadMigration);

		//display python plot
		Process proc = new System.Diagnostics.Process ();
		proc.StartInfo.FileName = "/bin/bash";
		proc.StartInfo.Arguments = "-c \"" + PYTHON_PLOT + "/python_plot.py c#" + " \"";
		proc.StartInfo.UseShellExecute = false; 
		proc.StartInfo.RedirectStandardOutput = false;
		proc.Start ();
	}

	static void ComputeElapsedTimeThreadMigration(int nrTriesThreadMigration) {
		long[] elapsedTimeThreadMigration = new long[nrTriesThreadMigration];

		for (int i = 0; i < nrTriesThreadMigration; i++) {
			elapsedTimeThreadMigration [i] = threadMigration ();
		}

		WriteToFile (THREAD_MIGRATION, elapsedTimeThreadMigration, nrTriesThreadMigration);
	}

	static void ComputeElapsedTimeThreadSwitchContext(int nrTriesThreadCreation) {
		long[] elapsedTimeThreadSuspend = new long[nrTriesThreadCreation];
		long[] elapsedTimeThreadResume = new long[nrTriesThreadCreation];

		for (int i = 0; i < nrTriesThreadCreation; i++) {
			long[] result = benchmarkLogic.ElapsedTimeThreadSwitchContext ();
			elapsedTimeThreadSuspend [i] = result [0];
			elapsedTimeThreadResume [i] = result [1];
		}

		WriteToFile (THREAD_SWITCH_CONTEXT_SUSPEND, elapsedTimeThreadSuspend, nrTriesThreadCreation);
		WriteToFile (THREAD_SWITCH_CONTEXT_RESUME, elapsedTimeThreadResume, nrTriesThreadCreation);
	}

	static void ComputeElapsedTimeThreadCreation(int nrTriesThreadCreation) {
		long[] elapsedTimeThreadCreation = new long[nrTriesThreadCreation];

		for (int i = 0; i < nrTriesThreadCreation; i++) {
			elapsedTimeThreadCreation [i] = benchmarkLogic.ElapsedTimeThreadCreation ();
		}

		WriteToFile (THREAD_CREATION, elapsedTimeThreadCreation, nrTriesThreadCreation);
	}

	static void ComputeElapsedTimeMemoryAccess(int nrTriesMemoryAccess) {
		long[] elapsedTimeMemoryAccess = new long[nrTriesMemoryAccess];

		for (int i = 0; i < nrTriesMemoryAccess; i++) {
			elapsedTimeMemoryAccess [i] = benchmarkLogic.ElapsedTimeMemoryAccess ();
		}

		WriteToFile (MEMORY_ACCESS, elapsedTimeMemoryAccess, nrTriesMemoryAccess);
	}

	static void ComputeElapsedTimeMemoryAllocation(int nrTriesMemoryAllocation,int sizeMemoryAllocation, bool gcOn) {
		long[] elapsedTimeMemoryAllocation = new long[nrTriesMemoryAllocation];
		long[] elapsedTimeMemoryDeallocation = new long[nrTriesMemoryAllocation];

		for(int i = 0;i<nrTriesMemoryAllocation;i++) {
			long[] result = benchmarkLogic.ElapsedTimeMemoryAllocationAndDeallocation (sizeMemoryAllocation, gcOn);
			elapsedTimeMemoryAllocation [i] = result [0];
			elapsedTimeMemoryDeallocation [i] = result [1];
		}
		if (gcOn) {
			WriteToFile (MEMORY_ALLOCATION, elapsedTimeMemoryAllocation, nrTriesMemoryAllocation);
			WriteToFile (MEMORY_ALLOCATION_GC_ON, elapsedTimeMemoryDeallocation, nrTriesMemoryAllocation);
		} else {
			WriteToFile (MEMORY_ALLOCATION_GC_OFF, elapsedTimeMemoryDeallocation, nrTriesMemoryAllocation);
		}
	}

	static void WriteToFile(string filename, long[] elapsedTime, long nrTries) {
		using (System.IO.StreamWriter file = 
			new System.IO.StreamWriter(filename))
		{
			foreach (long time in elapsedTime)
			{
				file.WriteLine(time);
			}
		}
	}
}