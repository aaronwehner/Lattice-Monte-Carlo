package Lattice.LatticeLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LammpsInput{

    public final File FILE;
    
    public LammpsInput(File FILE) throws IOException{
        if(!FILE.isFile() || !FILE.canRead() || ! FILE.exists()) throw new IOException("File cannot be used as LammpsInput");
        this.FILE = FILE;
    }

    public Stream<String> getHeader() throws IOException{
        return Files.lines(FILE.toPath())                       //creates a stream of the given file @path
            .skip(1)                                            //skip first (comment) line
            .filter(s -> !s.isBlank())                           //removes blank lines              
            .takeWhile(s -> !s.matches("\\s*[a-zA-Z].*"));      //read header, which end is defined by the beginning of a body section.
    }

    public Map<String, String> mapHeader() throws IOException{
        return this.getHeader()                                       
            .collect(Collectors.toMap(                                          //maps the keys to their values
                s -> s.replaceAll("#.*", "").replaceAll("^.*\\d\\s", "").strip(),       //removes trailing comments and all digits (by this we get the keys)
                s -> s.replaceAll("#.*", "").replaceAll("\\D*$", "").strip())           //removes trailing comments amd all non digits at the end of the line (by this we get the values)
            );
    }

    public Stream<String> getSection(String section) throws IOException{
        return Files.lines(FILE.toPath())
            .dropWhile(s -> !s.startsWith(section)) //find section
            .skip(2)                                //skip describing and empty line
            .takeWhile(s -> !s.isBlank());          //go to the end of the section 
    }
}