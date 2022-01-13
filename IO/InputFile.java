package IO;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public class InputFile extends File{

    public final Map<String, String> DATA;

    public InputFile(String pathname) throws IOException {
        super(pathname);
        if(!this.isFile() || !this.canRead() || !this.exists()) throw new IOException("Path to file does not work.");
        this.DATA = this.readInputFile();
    }

    public Map<String, String> readInputFile() throws IOException{
        Map<String, String> data = new TreeMap<String, String>();
        data.put("input_path", this.toPath().toString());
        try(BufferedReader br = new BufferedReader(new FileReader(this))){
            String line;
            while ((line = br.readLine()) != null) {
                if(line.isBlank() || line.startsWith("#")) continue;
                if(line.contains("=")){
                    String[] lineMap = line.split("=");
                    data.put(lineMap[0].strip(), lineMap[1].strip());
                } else if (line.contains("START")){
                    String key = line.replaceAll("\\s+START", "").strip();
                    String value = "";
                    while(!(line = br.readLine()).contains("END")){
                        if(line.isBlank() || line.startsWith("#")) continue;
                        value += line + IO.LINESEP;
                    }
                    data.put(key, value);
                }
            }
        }
        return data;
    }
}
