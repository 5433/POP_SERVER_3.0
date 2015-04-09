package pop.server;

import java.util.HashMap;

public final class MainDomain{
	
	int i = 8888;
	String mes = "";
	KeywordSearchServer kss;
	HashMap<Integer,KeywordSearchServer> serverCollector = new HashMap<>();
	
	public MainDomain(){
		executeService();
	}
	
	   public void executeService() {

               Thread thread = new Thread(new NewRunThread());
               thread.start();

               while (true) {
                   try {
                       if (KeywordSearchServer.newForum == true) {
                           i++;
                           mes = KeywordSearchServer.temp;
                           System.out.println(mes);
                           KeywordSearchServer kes = new KeywordSearchServer(i, mes.replace("create", ""));
                           serverCollector.put(i,kes);
                       }
                   } catch (NullPointerException e) {
                       // do nothing
                   }
               }
    }
	
	class NewRunThread implements Runnable{
            @Override
            public void run(){
		kss = new KeywordSearchServer(i," Main");
            }
	}
	
}