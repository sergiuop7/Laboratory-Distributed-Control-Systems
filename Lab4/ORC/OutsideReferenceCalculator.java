package Lab4.ORC;

import core.FuzzyPetriLogic.Executor.AsyncronRunnableExecutor;
import core.FuzzyPetriLogic.FuzzyDriver;
import core.FuzzyPetriLogic.FuzzyToken;
import core.FuzzyPetriLogic.PetriNet.FuzzyPetriNet;
import core.FuzzyPetriLogic.PetriNet.Recorders.FullRecorder;
import core.FuzzyPetriLogic.Tables.OneXOneTable;
import core.TableParser;
import Lab4.HeaterTankControllerComponent;
import Lab4.Plant;

import java.util.HashMap;
import java.util.Map;
public class OutsideReferenceCalculator {
    static String reader = "" +
            "{[<ZR,NL><ZR,NM><ZR,ZR><ZR,PM><ZR,PL>]" +
            " [<ZR,NL><ZR,NM><ZR,ZR><ZR,PM><ZR,PL>]" +
            " [<ZR,NL><ZR,NM><ZR,ZR><ZR,PM><ZR,PL>]" +
            " [<ZR,NL><ZR,NM><ZR,ZR><ZR,PM><ZR,PL>]" +
            " [<ZR,NL><ZR,NM><ZR,ZR><ZR,PM><ZR,PL>]}";
    static String t2Table = "{[<PL><PM><ZR><NM><NL>]}"; /* So when the temperature is NL>
                                                            the command should be PL
                                                            NM> pm
                                                            ZR stays the same
                                                            PM > it should be cooler so NM
                                                            PL> NL */
    private FuzzyPetriNet net;
    private int p1OustideTemInp;
    private int t2Out;
    private int p1RefInp;
    private FuzzyDriver outsideTempDriver;
    private FuzzyDriver tankWaterTemeDriver;
    private FullRecorder rec;
    private AsyncronRunnableExecutor execcutor;

    public OutsideReferenceCalculator(Plant plant, HeaterTankControllerComponent comp, long simPeriod) {

// Build the petri net here // your homework

        TableParser parser = new TableParser();
        net = new FuzzyPetriNet();

        int p0 = net.addPlace();
        net.setInitialMarkingForPlace(p0,FuzzyToken.zeroToken());

        p1RefInp = net.addInputPlace();

        int tr0Reader = net.addTransition(0, parser.parseTwoXTwoTable(reader)); /// ????  corrrect
        net.addArcFromPlaceToTransition(p1RefInp,tr0Reader,1);
        net.addArcFromPlaceToTransition(p0,tr0Reader,1);

        int p2 = net.addPlace();
        int p3 = net.addPlace();

        net.addArcFromTransitionToPlace(tr0Reader,p2);
        net.addArcFromTransitionToPlace(tr0Reader,p3);

        int t1 = net.addTransition(1, OneXOneTable.defaultTable());
        net.addArcFromPlaceToTransition(p2,t1,1);
        net.addArcFromTransitionToPlace(t1,p0);

        // t2-exit

        //int t2Out = net.addOuputTransition(parser.parseOneXOneTable(t2Table)); //shouldn't be defined again, use the same paramaeter from the attributes
        t2Out = net.addOuputTransition(parser.parseOneXOneTable(t2Table));
        net.addArcFromPlaceToTransition(p3,t2Out,1);

        outsideTempDriver = FuzzyDriver.createDriverFromMinMax(-30, 10);
        tankWaterTemeDriver = FuzzyDriver.createDriverFromMinMax(45, 68);
        net.addActionForOuputTransition(t2Out, tk -> { //<<<<<<<<<< this is the t2 action
            comp.setWaterRefTemp(tankWaterTemeDriver.defuzzify(tk));//<<< that is connected to
//<<< The water tank
        });
        rec = new FullRecorder();
        execcutor = new AsyncronRunnableExecutor(net, simPeriod);
        execcutor.setRecorder(rec);
    }
    public void start() {
        (new Thread(execcutor)).start(); }
    public void stop() { execcutor.stop(); }
    public void setOutsideTemp(double waterRefTemp) { //this method is responsible for the inputs you need to change p1 name here too
        Map<Integer, FuzzyToken> inps = new HashMap<Integer, FuzzyToken>();
        inps.put(p1RefInp, outsideTempDriver.fuzzifie(waterRefTemp)); //>>>>>>>>>>.changed here
        execcutor.putTokenInInputPlace(inps); }
    public FuzzyPetriNet getNet() { return net; } public FullRecorder getRecorder() { return rec; }
}