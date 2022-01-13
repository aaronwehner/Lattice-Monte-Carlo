package IO;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.io.BufferedWriter;
import java.io.File;

public class Output extends IO{

    public final String MAINPATH;

    public Output(String MAINPATH){
        this.MAINPATH =  MAINPATH;
        createDirs(this.MAINPATH);
        System.out.println("Output Data is saved in: " + this.MAINPATH);
    }
    
    /** 
     * Returns the main output Path.
     * @return String String represenation of the main output path.
     */
    public String getMainpath(){
        return MAINPATH;
    }
    
    /** 
     * Creates the directories for the give path.
     * @param path String representation of the path which directories should be created.
     */
    public void createDirs(String path){
        File directory = new File(path);
        directory.mkdirs();
    }
    
    /** 
     * Creates a BufferedWriter for a file in the output path or in a subpath.
     * @param   subpath         The subpath in which the file is saved. If blank the output path is used directly.
     * @param   filename        The name of the file where the output is saved.
     * @return  BufferedWriter  The BufferedWriter that can be used to save data to the file.
     * @throws IOException
     */
    public BufferedWriter getBufferedWriter(String subpath, String filename) throws IOException{ 
        String path;
        if(subpath.isBlank()) path = MAINPATH + PATHSEP;
        else path = MAINPATH + PATHSEP + subpath + PATHSEP; 
        File directory = new File(path);
        BufferedWriter bw = null;
        directory.mkdirs();
        File file = new File(path + filename);
        if(file.exists()){
            System.out.println("File already exists. Program is terminated.");
            System.exit(0);
        }
        try{
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            bw = new BufferedWriter(fw);
        }
        catch (IOException e){
            throw new IOException("Failed to get BufferedWriter for file \"" + filename + "\" : " + e.getLocalizedMessage());
        } 
        return bw; 
    }

    /**
     * Writes a String into a file.
     * @param string    The String to be written.
     * @param subpath   The subpath in the output directory. If blank the output directory is used directly.
     * @param filename  The name of the file to be written.
     * @throws IOException
     */
    public void writeString(String string, String subpath, String filename) throws IOException {
        try {
            BufferedWriter bufferedWriter = getBufferedWriter(subpath, filename);
            bufferedWriter.write(string);
            bufferedWriter.close();
        } catch (Exception e) {
            throw new IOException("Failed to write list to file \"" + filename + "\" : " + e.getLocalizedMessage());
        }
    }

    /** 
     * Writes an ArrayList into a file.
     * @param arrayList The ArrayList to be written, containing the lines.
     * @param subpath   The subpath in the output directory. If blank the output directory is used directly.
     * @param filename  The name of the file to be written.
     */
    public void writeList(List<Object> arrayList, String subpath, String filename) throws IOException{
        try {
            BufferedWriter bufferedWriter = getBufferedWriter(subpath, filename);
            for(Object obj: arrayList){
                bufferedWriter.write(obj + LINESEP);
            }
            bufferedWriter.close();
        }
        catch (Exception e) {
            throw new IOException("Failed to write list to file \"" + filename + "\" : " + e.getLocalizedMessage());
        }
    }

    /** 
     * Writes an ArrayList containing columns into a file.
     * @param arrayList The ArrayList to be written, containing the lines and columns represented by another ArrayList.
     * @param subpath   The subpath in the output directory. If blank the output directory is used directly.
     * @param filename  The name of the file to be written.
     */
    public void writeListWithColumns(List<Object[]> arrayList, String subpath, String filename) throws IOException{
        try {
            BufferedWriter bufferedWriter = getBufferedWriter(subpath, filename);
            for(Object[] line: arrayList){
                String lineAsString = "";
                for(Object obj:line){
                    lineAsString += obj.toString() + TAB;
                }
                bufferedWriter.write(lineAsString + LINESEP);
            }
            bufferedWriter.close();
        }
        catch (Exception e) {
            throw new IOException("Failed to write list to file \"" + filename + "\" : " + e.getLocalizedMessage());
        }
    }

    /** 
     * Writes an ArrayList into a file, enumerating the lines.
     * @param arrayList The ArrayList to be written, containing the lines.
     * @param subpath   The subpath in the output directory. If blank the output directory is used directly.
     * @param filename  The name of the file to be written.
     */
    public void writeEnumeratedList(List<Object> arrayList, String subpath, String filename) throws IOException{
        try {
            BufferedWriter bufferedWriter = getBufferedWriter(subpath, filename);
            int counter = 0;
            for(Object obj: arrayList){
                bufferedWriter.write("" + counter + TAB + obj + LINESEP);
                counter++;
            }
            bufferedWriter.close();
        }
        catch (Exception e) {
            throw new IOException("Failed to write list to file \"" + filename + "\" : " + e.getLocalizedMessage());
        }
    }

    public void writeMap(Map<?, ?> map, String subpath, String filename) throws IOException{
        try {
            BufferedWriter bufferedWriter = getBufferedWriter(subpath, filename);
            for(Entry<?, ?> obj: map.entrySet()){
                bufferedWriter.write(obj.getKey() + IO.TAB + obj.getValue() + LINESEP);
            }
            bufferedWriter.close();
        }
        catch (Exception e) {
            throw new IOException("Failed to write map to file \"" + filename + "\" : " + e.getLocalizedMessage());
        }
    }
}

