package simulator.launcher;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import simulator.factories.SetContClassEventBuilder;
import simulator.factories.SetWeatherEventBuilder;
import simulator.model.DequeuingStrategy;
import simulator.model.Event;
import simulator.model.LightSwitchingStrategy;
import simulator.model.TrafficSimulator;
import simulator.control.Controller;
import simulator.factories.Builder;
import simulator.factories.BuilderBasedFactory;
import simulator.factories.Factory;
import simulator.factories.MostCrowdedStrategyBuilder;
import simulator.factories.NewCityRoadEventBuilder;
import simulator.factories.NewInterCityRoadEventBuilder;
import simulator.factories.NewJunctionEventBuilder;
import simulator.factories.NewVehicleEventBuilder;
import simulator.factories.MoveAllStrategyBuilder;
import simulator.factories.MoveFirstStrategyBuilder;
import simulator.factories.RoundRobinStrategyBuilder;
import simulator.view.MainWindow;

public class Main {
	private final static Integer _timeLimitDefaultValue = 10;
	private static String _inFile = null;
	private static String _outFile = null;
	private static Factory<Event> _eventsFactory = null;
	private static Integer _timeLimit;
	private static boolean _guiMode = true;

	private static void parseArgs(String[] args) {

		// define the valid command line options
		Options cmdLineOptions = buildOptions();

		// parse the command line as provided in args
		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine line = parser.parse(cmdLineOptions, args);
			parseHelpOption(line, cmdLineOptions);
			parseModeOption(line);
			parseInFileOption(line);
			
			if(!_guiMode) {
				parseOutFileOption(line);
				parseTicksOption(line);
			}

			// if there are some remaining arguments, then something wrong is
			// provided in the command line!
			//
			String[] remaining = line.getArgs();
			if (remaining.length > 0) {
				String error = "Illegal arguments:";
				for (String o : remaining)
					error += (" " + o);
				throw new ParseException(error);
			}

		} catch (Exception e) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(Main.class.getCanonicalName(), cmdLineOptions, true);
			System.exit(1);
		}

	}

	private static Options buildOptions() {
		Options cmdLineOptions = new Options();

		cmdLineOptions.addOption(Option.builder("h").longOpt("help").desc("Print this message").build());
		cmdLineOptions.addOption(Option.builder("i").longOpt("input").hasArg().desc("Events input file").build());
		cmdLineOptions.addOption(Option.builder("o").longOpt("output").hasArg().desc("Output file, where reports are written.").build());
		cmdLineOptions.addOption(Option.builder("t").longOpt("ticks").hasArg().desc("Ticks to the simulatorís main loop (default value is 10).").build());
		cmdLineOptions.addOption(Option.builder("m").longOpt("mode").hasArg().desc("Mode: gui or console").build());
		
		return cmdLineOptions;
	}

	private static void parseHelpOption(CommandLine line, Options cmdLineOptions) {
		if (line.hasOption("h")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(Main.class.getCanonicalName(), cmdLineOptions, true);
			System.exit(0);
		}
	}

	private static void parseInFileOption(CommandLine line) throws ParseException {
		_inFile = line.getOptionValue("i");
		if (_inFile == null && !_guiMode) {
			throw new ParseException("An events file is missing");
		}
	}
	
	private static void parseOutFileOption(CommandLine line) throws ParseException {
		_outFile = line.getOptionValue("o");
	}
	
	private static void parseTicksOption(CommandLine line) throws ParseException {
		try {
			_timeLimit = Integer.parseInt(line.getOptionValue("t"));
		}
		catch(Exception e) {}
		if(_timeLimit == null)
			_timeLimit = _timeLimitDefaultValue;
	}

	
	private static void parseModeOption(CommandLine line) throws Exception {
		String option = line.getOptionValue("m");
		if(option != null) {
			switch(option) {
			case "console":
				_guiMode = false;
				break;
			case "gui":
				break;
			default:
				throw new Exception();
			}
		}
	}
	
	private static void initFactories() {
		//Llamada Builders de cambio de Semaforo
		List<Builder<LightSwitchingStrategy>> lsbs = new ArrayList<>();
		lsbs.add(new RoundRobinStrategyBuilder());
		lsbs.add(new MostCrowdedStrategyBuilder());
		Factory<LightSwitchingStrategy> lssFactory = new BuilderBasedFactory<>(lsbs);
		
		//Llamada Builders de extraccion de cola
		List<Builder<DequeuingStrategy>> dqbs = new ArrayList<>();
		dqbs.add(new MoveFirstStrategyBuilder());
		dqbs.add(new MoveAllStrategyBuilder());
		Factory<DequeuingStrategy> dqsFactory = new BuilderBasedFactory<>(dqbs);
		
		//Llamada Bulders de Factory		
		List<Builder<Event>> ebs = new ArrayList<>();
		ebs.add(new NewJunctionEventBuilder(lssFactory, dqsFactory));
		ebs.add(new NewCityRoadEventBuilder());
		ebs.add(new NewInterCityRoadEventBuilder());
		ebs.add(new NewVehicleEventBuilder());
		ebs.add(new SetWeatherEventBuilder());
		ebs.add(new SetContClassEventBuilder());
		
		_eventsFactory = new BuilderBasedFactory<>(ebs);
	}

	private static void startBatchMode() throws IOException {
		TrafficSimulator simulator = new TrafficSimulator();
		Controller control = null;
		try {
			control = new Controller(simulator, _eventsFactory);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		InputStream in = new FileInputStream(_inFile);
		OutputStream out = new FileOutputStream(_outFile);
		try {
			control.loadEvents(in);
		} catch (Exception e) {
			e.printStackTrace();
		}
		control.run(_timeLimit, out);
		in.close();
	}
	
	private static void startGuiMode() throws IOException {
		TrafficSimulator simulator = new TrafficSimulator();
		Controller control = null;
		
		try {
			control = new Controller(simulator, _eventsFactory);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		new MainWindow(control);
		
		if(_inFile != null) {
			InputStream in = new FileInputStream(_inFile);
			try {
				control.loadEvents(in);
			} catch (Exception e) {
				e.printStackTrace();
			}
			in.close();
		}
		
		System.out.println("Iniciado el simulador.");
	}

	private static void start(String[] args) throws IOException {
		initFactories();
		parseArgs(args);
		if(_guiMode==true) 
			startGuiMode();		
		else
		startBatchMode();
	}

	// example command lines:
	//
	// -i resources/examples/ex1.json
	// -i resources/examples/ex1.json -t 300
	// -i resources/examples/ex1.json -o resources/tmp/ex1.out.json
	// --help

	public static void main(String[] args) {
		try {
			start(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
