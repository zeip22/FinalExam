import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: sivabudh
 * Date: 3/17/13
 * Time: 2:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class AlienComm {
    public static void main(String[] args)
    {
        try {
            System.out.println("Initiaing communication system...");

            int i = 0;
            while(AlienLib.isAlienTransmitting())
            {
                ArrayList<AlienLib.Garble> signals = AlienLib.getAlienSignals();

                ArrayList<AlienLib.Data> data = new ArrayList<AlienLib.Data>();
                for(AlienLib.Garble g : signals) {
                    data.add(AlienLib.processAlienSignal(g));
                }

                AlienLib.transmitToPrimeMinisterOffice(data);

                System.out.println("Pass: "+ i++ +" with "+ data.size()+" signals.");
            }

            System.out.println("Done processing alien's data.");
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }
}