import lexical.ArrayStringTable;
import lexical.StringTable;
import nodetype.NodeType;
import org.w3c.dom.Document;
import semantic.StackSymbolTable;
import syntax.Program;
import syntax.template.XMLTemplate;
import template.CTemplate;
import visitor.CodeGeneratorVisitor;
import visitor.ConcreteVisitor;
import visitor.ScopeCheckerVisitor;
import visitor.TypeCheckerVisitor;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class MyFun2C {

    static Lexer lexer;
    static Parser parser;
    private static String workingDir = System.getProperty("user.dir");

    public static void main(String[] args) throws Exception {
        StringTable stringTable = new ArrayStringTable();
        lexer = new Lexer(stringTable);
        StackSymbolTable symbolTable = new StackSymbolTable(stringTable);

        if(lexer.initialize(args[0])) {
            parser = new Parser(lexer);

            Program program = (Program) parser.parse().value;

            //XML ABSTRACT SINTAX TREE
            XMLTemplate xmlTemplate = new XMLTemplate();
            Document xmlDocument = xmlTemplate.create().get();
            ConcreteVisitor xmlVisitor = new ConcreteVisitor();
            program.accept(xmlVisitor, xmlDocument);
            //creazione file.xml
            String dir = "test_files";
            String filename="";
            String fullName = args[0];
            if(fullName.contains(File.separator)){
                filename = fullName.substring(fullName.lastIndexOf(File.separator)+1,fullName.length()-4);
                System.out.println("FILENAME "+filename);
            }else{
                filename = fullName;
            }

            xmlTemplate.write( dir +File.separator+ "ast_out"+File.separator + filename + ".xml", xmlDocument);
            System.out.println("Abstract Sintax Tree in xml generato!");
            //STAMPA STRING TABLE
            System.out.println("String Table:\n" + stringTable.toString());

            //VISITORS
            ScopeCheckerVisitor scopeCheckerVisitor = new ScopeCheckerVisitor();
            TypeCheckerVisitor typeCheckerVisitor = new TypeCheckerVisitor();
            CodeGeneratorVisitor codeGeneratorVisitor = new CodeGeneratorVisitor();

            //SCOPE CHECK
            program.accept(scopeCheckerVisitor, symbolTable);
            System.out.println("Symbol table:\n"+ symbolTable.toString());
            symbolTable.resetLevel();

            //TYPE CHECK
            NodeType typeCheck = program.accept(typeCheckerVisitor, symbolTable);
            System.out.println("Type Checking Completato: " + typeCheck.toString());
            symbolTable.resetLevel();

            //GENERATE C CODE
            String path = dir +File.separator+ "c_out"+ File.separator+ filename + ".c";
            CTemplate cTemplate = new CTemplate();
            File cfile = cTemplate.create(path).get();
            String root = program.accept(codeGeneratorVisitor, symbolTable);
            cTemplate.write(cfile, root);

            //COMPILE C SOURCE FILE
            //compileCprog(filename);
            //APRE TERMINALE
            //openTerminal();
        } else {
            System.out.println("File not found!");
        }
    }

    //compila il file C
    public static void compileCprog(String filename) throws IOException, InterruptedException {
        try {
            boolean exist = Files.exists(Path.of("test_files\\exe_out\\" + filename+ ".exe"));
            if(exist)
                Files.delete(Path.of("test_files\\exe_out\\" + filename+ ".exe"));
        } catch (Exception x) {
            System.err.format(x.toString());
        }

        ProcessBuilder builder = new ProcessBuilder();
        final List<String> commandsCompile = new ArrayList<>();

        commandsCompile.add("cmd.exe");
        commandsCompile.add("/C");
        commandsCompile.add("gcc" + " test_files\\c_out\\" + filename + ".c " + "-o " + "test_files\\exe_out\\" + filename);

        builder.command(commandsCompile);

        builder.directory(new File(workingDir));
        Process process = builder.start();
        StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), System.out::println);
        Executors.newSingleThreadExecutor().submit(streamGobbler);
        process.waitFor();
    }

    //apre il terminale per eseguire l'eseguibile
    public static void openTerminal() throws IOException, InterruptedException {
        Process p = Runtime.getRuntime().exec("cmd.exe /c start ");
        int exitCode = p.waitFor();
        assert exitCode == 0;
        System.exit(0);
    }
}
