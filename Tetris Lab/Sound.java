import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.sound.sampled.*;

public class Sound
{
    private static File file;
    private static AudioInputStream ais;
    private static Clip clip;

    public static void setFile(String fileName)
    {
        file = new File(fileName);
    }
    public static void playSong(String fileName)
    {
        file = new File(fileName);
    }
}