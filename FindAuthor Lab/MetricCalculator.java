import java.util.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileReader;

/**
 * 
 */
public class MetricCalculator 
{
    private Document currDoc;
    private final double[] WEIGHTS = {0, 11, 33, 50, 0.4, 4};
    private HashMap<String, double[]> signatures;

    /**
     * Constructs a new MetricCalculator, initializing the map of signatures with the .stats files
     * in the SignatureFiles folder with their author and their signature
     */
    public MetricCalculator()
    {
        BufferedReader reader;
        signatures = new HashMap<String, double[]>();

        try 
        {
            for(File f : new File("./SignatureFiles").listFiles())
            {
                if(f.getName().endsWith(".stats"))
                {
                    reader = new BufferedReader(new FileReader(f));
                    String author = reader.readLine();
                    double[] values = new double[5];
                    for(int i=0; i<5; i++)
                    {
                        values[i] = Double.parseDouble(reader.readLine());
                    }
                    signatures.put(author, values);
                }
            }
        } 
        catch (FileNotFoundException e) 
        {
            System.out.println("Signature Files folder does not exist");
        }
        catch (IOException e) 
        {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Calculates and stores the linguistic signature of a document, calculating based on avg word length,
     * type-token ratio, Hapax-Legomana ratio, avg words per sentence, and phrases per sentence
     * 
     * Prints the most similar author to this document's author
     * 
     * @param docNum the number of the mystery document to analyze
     */
    public void calcDoc(String fileName)
    {
        currDoc = new Document(fileName);
        currDoc.parseDocument();

        double[] values = new double[6];

        int letters = 0;
        int words = 0;
        int phrases = 0;
        int sentences = currDoc.getSentences().size();

        HashMap<Token, Integer> tokenMap = new HashMap<Token, Integer>();

        for(Sentence s : currDoc.getSentences())
        {
            for(Phrase p : s.sentenceCopy())
            {
                phrases++;
                for(Token t : p.phraseCopy())
                {
                    if(t.getType()== (Scanner.TOKEN_TYPE.WORD))
                    {
                        words++;
                        letters += t.getValue().length();
                        if(tokenMap.containsKey(t))
                        {
                            tokenMap.put(t, tokenMap.get(t)+1);
                        }
                        else
                        {
                            tokenMap.put(t, 1);
                        }
                    }
                }
            }
        }

        String author = /*currDoc.getAuthor()*/ fileName;

        values[1] = ((double) letters)/words;
        values[2] = ((double) tokenMap.keySet().size())/words;
        int uniqueWords = 0;
        for(Integer i : tokenMap.values())
        {
            if(i==1)
                uniqueWords++;
        }
        values[3] = ((double) uniqueWords)/words;
        values[4] = ((double) words)/sentences;
        values[5] = ((double) phrases)/sentences;

        System.out.println("\nStats for "+author
                            +"\nletters: "+letters
                            +"\nwords: "+words
                            +"\ndifferent words: "+tokenMap.keySet().size()
                            +"\nunique words: "+uniqueWords
                            +"\nphrases: "+phrases
                            +"\nsentences: "+sentences);
        for(double d : values)
            System.out.println(d);
        
        findSimilar(author, values);

        /*signatures.put(author, signature);

        try 
        {
            List<String> lines = new ArrayList<String>(6);
            lines.add(author);
            for(int i=1; i<values.length; i++)
                lines.add(Double.toString(values[i]));
            Path file = Paths.get("./SignatureFiles/"+author.substring(0, author.indexOf(" "))
                                    +"."+author.substring(author.indexOf(" ")+1)+".stats");
            Files.write(file, lines);
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
            System.exit(-1);
        }*/
    }

    /**
     * Prints the differences between the signature given and all the signatures stored, finds the signature
     * with the smallest difference, being the author most similar to the given author
     * @param author the name of the author
     * @param signature an author's calculated writing signature
     */
    private void findSimilar(String author, double[] values)
    {
        double minDiff = Double.MAX_VALUE;
        String closestAuthor = "";
        for(Map.Entry<String, double[]> e : signatures.entrySet())
        {
            double signatureDiff = 0;
            for(int i=0; i<5; i++)
            {
                signatureDiff += Math.abs((e.getValue()[i] - values[i+1]) * WEIGHTS[i+1]);
            }
            if(signatureDiff < minDiff)
            {    
                closestAuthor = e.getKey();
                minDiff = signatureDiff;
            }
        }
        System.out.println(author+"'s closest match is: "+closestAuthor+" with a difference of "+minDiff);
    }
}
