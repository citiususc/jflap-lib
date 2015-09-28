/*
 *  JFLAP - Formal Languages and Automata Package
 * 
 * 
 *  Susan H. Rodger
 *  Computer Science Department
 *  Duke University
 *  August 27, 2009

 *  Copyright (c) 2002-2009
 *  All rights reserved.

 *  JFLAP is open source software. Please see the LICENSE for terms.
 *
 */




package edu.duke.cs.jflap.gui.action;

import java.awt.Component;
import javax.swing.JComponent;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import edu.duke.cs.jflap.gui.editor.EditorPane;
import edu.duke.cs.jflap.gui.environment.Universe;




/**
  This utility was created to factor out the massive amounts of common code in the four Graphics saving action classes.
  */
public class SaveGraphUtility{

        public static void saveGraph(Component apane, JComponent c, String description,  String format){
           
           if (apane instanceof EditorPane){
               apane = ((EditorPane)apane).getAutomatonPane();
           }

           Image canvasimage = apane.createImage(apane.getWidth(),apane.getHeight());
           Graphics imgG = canvasimage.getGraphics();
           apane.paint(imgG);
           BufferedImage bimg = new BufferedImage(canvasimage.getWidth(null), canvasimage.getHeight(null), BufferedImage.TYPE_INT_RGB);
           Graphics2D g = bimg.createGraphics();
           g.drawImage(canvasimage, null, null);
           


           Universe.CHOOSER.resetChoosableFileFilters();
           Universe.CHOOSER.setAcceptAllFileFilterUsed(false);
        
           FileFilter spec = new FileNameExtensionFilter(description, format.split(","));

           Universe.CHOOSER.addChoosableFileFilter(spec);
           Universe.CHOOSER.addChoosableFileFilter(new AcceptAllFileFilter());
           Universe.CHOOSER.setFileFilter(spec);


           int result = Universe.CHOOSER.showSaveDialog(c);
           while (result == JFileChooser.APPROVE_OPTION) {
                    File file = Universe.CHOOSER.getSelectedFile();

                    if (!new FileNameExtensionFilter(description, format.split(",")).accept(file)) //only append if the chosen name is not acceptable
                        file = new File(file.getAbsolutePath() + "." + format.split(",")[0]);

                    if (file.exists()) {
                        int confirm = JOptionPane.showConfirmDialog(Universe.CHOOSER, "File exists. Shall I overwrite?", "FILE OVERWRITE ATTEMPTED", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.NO_OPTION){ 
                            result = Universe.CHOOSER.showSaveDialog(c);
                            continue;
                        }
                    }
                
             try  
             {
                    ImageIO.write(bimg, format.split(",")[0], file);
                    return;
              } 
             catch (IOException ioe)  
             {  
                    JOptionPane.showMessageDialog(c,
                    "Save failed with error:\n"+ioe.getMessage(),
                    "Save failed", JOptionPane.ERROR_MESSAGE);
                    return;
             }
		}
        }

}
/**
  
* Java 6 has this, but not previous versions of java, so I'm writing it here.

* @author Henry
*/

class FileNameExtensionFilter extends FileFilter{
    String[] myAcceptedFormats;
    String myDescription;
    public FileNameExtensionFilter(String description, String... formats){
        myDescription = description;
        myAcceptedFormats = formats; 
    }

    public boolean accept(File f){
        if (f.isDirectory()) return true;
        for (int i = 0; i < myAcceptedFormats.length; i++)
            if (f.getName().endsWith("."+myAcceptedFormats[i])) return true;
        return false;
    }
    public String getDescription(){
        return myDescription;
    }
}

class AcceptAllFileFilter extends FileFilter
{
    public boolean accept(File f){
        return true;
    }
    public String getDescription(){
        return "All files";
    }
}

