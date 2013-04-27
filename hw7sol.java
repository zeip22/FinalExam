import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
public class huhu7 {
	public static void main (String[]args){
		final long start = System.nanoTime();
		try{
			System.out.println("Initiaing communication system...\n");
			BlockingQueue<ArrayList<AlienLib.Garble>> appt1 = new LinkedBlockingQueue<ArrayList<AlienLib.Garble>>(7);
			BlockingQueue<ArrayList<AlienLib.Data>> appt2 = new LinkedBlockingQueue<ArrayList<AlienLib.Data>>(7);;
			final CountDownLatch cdl = new CountDownLatch(7);
			Thread a1 = new GetHuhu(appt1);
			Thread a2 = new ProcessHuhu(appt1,appt2);
			Thread a3 = new TransmitHuhu(appt2,cdl);
			a1.start();
			a2.start();
			a3.start();
			cdl.await();
			final long end = System.nanoTime();
			System.out.println("Time Used: "+(end-start)/1.0e9+" sec");
		}catch(InterruptedException e){}
	}
	public static class GetHuhu extends Thread {
	private final BlockingQueue<ArrayList<AlienLib.Garble>> appt1;
	public GetHuhu(BlockingQueue<ArrayList<AlienLib.Garble>> lbq) {
	appt1 = lbq;
	}
@Override
	public void run(){
		while (AlienLib.isAlienTransmitting()) {
			try {
			ArrayList<AlienLib.Garble> signals = AlienLib.getAlienSignals();
			appt1.add(signals);
			} catch (Exception e) {
			}
		}
	}
	}
	public static class ProcessHuhu extends Thread {
		private final BlockingQueue<ArrayList<AlienLib.Garble>> appt1;
		private final BlockingQueue<ArrayList<AlienLib.Data>> appt2;
		private boolean shutdown = true;
		public final static int NUM_THREADS = Runtime.getRuntime().availableProcessors();
	public ProcessHuhu(BlockingQueue<ArrayList<AlienLib.Garble>> lbq1,BlockingQueue<ArrayList<AlienLib.Data>> lbq2) {
		appt1 = lbq1;
		appt2 = lbq2;
	}
	@Override
	public void run(){
		while (shutdown) {	
			try {
				ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
				ArrayList<AlienLib.Garble> signals = appt1.take();
				ArrayList<AlienLib.Data> data = new ArrayList<AlienLib.Data>();
				final int partitionSize = signals.size()/ NUM_THREADS;
				List<TaskExecutor> workers = new LinkedList<TaskExecutor>();
				for(int i = 0; i < NUM_THREADS; i++)
				{
				final int from = i * partitionSize;
				final int to = from + partitionSize;
				workers.add( new TaskExecutor(signals, from, to) );
				}
			try{
				List<Future<ArrayList<AlienLib.Data>>> results = executor.invokeAll(workers);
				for(Future<ArrayList<AlienLib.Data>> g : results) {
				data.addAll(g.get()); //**addAll**
				}
				executor.shutdown();
				} catch(Exception e) {}
		appt2.add(data);
		}
		catch(Exception e)
		{
		shutdown = false;
		}
		}
	}
}
	public static class TransmitHuhu extends Thread {
		int i = 0;
		CountDownLatch latch;
		private final BlockingQueue<ArrayList<AlienLib.Data>> appt2;
		private boolean shutdown = true;
		public TransmitHuhu(BlockingQueue<ArrayList<AlienLib.Data>> data, CountDownLatch latch){
		appt2 = data;
		this.latch = latch;
	}
	public void run(){
		while(shutdown){
			try{
			ArrayList<AlienLib.Data> data = appt2.take();
			AlienLib.transmitToPrimeMinisterOffice(data);
			System.out.println("Pass: "+ i++ +" with "+ data.size()+" signals.");
			latch.countDown();
			}catch (Exception e){
				shutdown = false;
				}
			}
		}
	}
public static class TaskExecutor implements Callable<ArrayList<AlienLib.Data>> {
private ArrayList<AlienLib.Garble> signals;
private int from;
private int to;
TaskExecutor(ArrayList<AlienLib.Garble> signals, int from, int to) {
this.signals = signals;
this.from = from;
this.to = to;
}
	public ArrayList<AlienLib.Data> call()
	{
	ArrayList<AlienLib.Data> result = new ArrayList<AlienLib.Data>();
		try 
		{
		for (int i=from;i<to;i++) 
		{
		result.add(AlienLib.processAlienSignal(signals.get(i)));
		}
		} 
		catch (Exception e) {}
		return result;
		}	
	}
}