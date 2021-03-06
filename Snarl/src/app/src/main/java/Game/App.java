/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package Game;

import Game.Common.InputParser;
import Remote.*;
import org.apache.commons.cli.*;
import java.io.File;

import org.json.*;


public class App {

    public static void main(String[] args) throws Exception {

        CmdLineOption cmdLineOption = parseCommandLineOptions(args);
        InputParser inputParser = new FileInputParser(cmdLineOption.getLevelsFileName());
        JSONArray levelList = inputParser.getLevelArray();

        if(cmdLineOption.getType().equals("client")) {
            System.out.println("Starting client...");
            Client client = new Client(cmdLineOption.getAddress(), cmdLineOption.getPort());
            client.startConnection();
        } else {
            System.out.println("Starting server...");
            Server server = new Server(cmdLineOption.getAddress(), cmdLineOption.getPort(), cmdLineOption.getClients(),
                    levelList, cmdLineOption.getIsObserverMode(), cmdLineOption.getWait(), cmdLineOption.getGames());
            server.startConnection();
        }
    }

    /**
     * Parse command line options
     * @param args
     * @return CmdLineOption
     * @throws Exception
     */
    private static CmdLineOption parseCommandLineOptions(String[] args) throws Exception {
        Options options = new Options();
        Option option = Option.builder("l").longOpt("levels").hasArg()
                .desc("file path for file containing the levels blueprint").build();
        Option clientOpt =  Option.builder("c").longOpt("clients").hasArg()
                .desc("number of clients in the game").build();
        Option waitOpt =  Option.builder("w").longOpt("wait").hasArg()
                .desc("number of clients in the game").build();
        Option levelStartOpt = Option.builder("s").longOpt("start").hasArg()
                .desc("level to start from").build();
        Option observe = new Option("o", "observe", false, "observer view or not");
        Option addressOpt = Option.builder("a").longOpt("address").hasArg()
                .desc("IP address").build();
        Option portOpt = Option.builder("po").longOpt("port").hasArg()
                .desc("port number").build();
        Option typeOpt = Option.builder("t").longOpt("type").hasArg()
                .desc("Indicates this is a server or client").build();
        Option gamesOpt = Option.builder("g").longOpt("games").hasArg()
                .desc("Indicates how many games will be played").build();

        options.addOption(option);
        options.addOption(levelStartOpt);
        options.addOption(observe);
        options.addOption(addressOpt);
        options.addOption(portOpt);
        options.addOption(typeOpt);
        options.addOption(waitOpt);
        options.addOption(clientOpt);
        options.addOption(gamesOpt);
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;
        // set default command line options if not given
        try {
            cmd = parser.parse(options, args);
            String filename = cmd.getOptionValue("levels") == null ? new File("").getAbsolutePath()+"/snarl.levels": cmd.getOptionValue("levels");
            int clients = cmd.getOptionValue("clients") == null? 1 : Integer.parseInt(cmd.getOptionValue("clients"));
            int levelToStart = cmd.getOptionValue("start") == null ? 1 : Integer.parseInt(cmd.getOptionValue("start"));
            boolean observer = cmd.hasOption("observe");
            String address = cmd.getOptionValue("address") == null? "localhost" : cmd.getOptionValue("address");
            String type = cmd.getOptionValue("type") == null? "server" : cmd.getOptionValue("type");
            int port = cmd.getOptionValue("port") == null ? 45678 : Integer.parseInt(cmd.getOptionValue("port"));
            int wait = cmd.getOptionValue("wait") == null? 60 : Integer.parseInt(cmd.getOptionValue("wait"));
            if(!type.equals("server") && !type.equals("client")) {
                throw new Exception("Must be client or server");
            }
            int games = cmd.getOptionValue("games") == null? 1 : Integer.parseInt(cmd.getOptionValue("games"));

            return new CmdLineOption(filename, levelToStart, observer, address, port, type, wait, clients, games);

        } catch (ParseException e) {
            System.out.println(e);
            formatter.printHelp("utility-name", options);
            System.exit(1);
        }
        return new CmdLineOption(new File("").getAbsolutePath()+"/snarl.levels", 1, false,
                "localhost", 12345, "server", 60, 1, 1);
    }
}
