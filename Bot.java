/* HOW TO RUN
   1) Configure things in the Configuration class
   2) Compile: javac Bot.java
   3) Run in loop: while true; do java Bot; sleep 1; done
*/
import java.lang.*;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.Socket;
import java.util.ArrayList;

class Configuration {
    String exchange_name;
    int    exchange_port;
    /* 0 = prod-like
       1 = slow
       2 = empty
    */
    final Integer test_exchange_kind = 0;
    /* replace REPLACEME with your team name! */
    final String  team_name          = "CALTRADERS";

    Configuration(Boolean test_mode) {
        if(!test_mode) {
            exchange_port = 20000;
            exchange_name = "production";
        } else {
            exchange_port = 20000 + test_exchange_kind;
            exchange_name = "test-exch-" + this.team_name;
        }
    }

    String  exchange_name() { return exchange_name; }
    Integer port()          { return exchange_port; }
}

public class Bot
{
    public static void main(String[] args)
    {
        /* The boolean passed to the Configuration constructor dictates whether or not the
           bot is connecting to the prod or test exchange. Be careful with this switch! */
        Configuration config = new Configuration(false);
        try
        {
            Socket skt = new Socket(config.exchange_name(), config.port());
            BufferedReader from_exchange = new BufferedReader(new InputStreamReader(skt.getInputStream()));
            PrintWriter to_exchange = new PrintWriter(skt.getOutputStream(), true);

            /*
              A common mistake people make is to to_exchange.println() > 1
              time for every from_exchange.readLine() response.
              Since many write messages generate marketdata, this will cause an
              exponential explosion in pending messages. Please, don't do that!
            */
            to_exchange.println(("HELLO " + config.team_name).toUpperCase());
            String reply = from_exchange.readLine().trim();
            System.err.printf("The exchange replied: %s\n", reply);


            String[] secruityTypes = {"BOND", "VALBZ", "VALE", "GS", "MS", "WFC", "XLF"};
            int[] limits = {100, 10, 10, 100, 100, 100, 100};
            int[] quantityHeld = new int[7];

            int[] buyQuantity = new int[7];
            int[] sellQuantity = new int[7];
            int[] sellPrice = {1001,4201,4181,8415,4002,5601,4223};
            int[] buyPrice = {992,4150,4130,8370,3800,5500,4152};
            int[] highSellValue = new int[7];
            int[] lowSellValue = new int[7];
            double[] momentumperTime = new double[7];


            for(int i = 0; i < limits.length; i++){
                buyQuantity[i] = (int) Math.floor(limits[i]/10);
                sellQuantity[i] = buyQuantity[i];
            }

            ArrayList<String> sellSecurities = new ArrayList<>();
            ArrayList<String> buySecurities = new ArrayList<>();


            int id = 0;
            for(String SecurityType: secruityTypes){
                to_exchange.println(("ADD " + id + " " + SecurityType + " BUY" + " "+ buyPrice[id] +" "+ buyQuantity[id]).toUpperCase());
                to_exchange.println(("ADD " + id + " " + SecurityType + " SELL" +" "+ sellPrice[id] +" "+ sellQuantity[id]).toUpperCase());
                id++;
            }


        }

        catch (Exception e)
        {
            e.printStackTrace(System.out);
        }
    }

}