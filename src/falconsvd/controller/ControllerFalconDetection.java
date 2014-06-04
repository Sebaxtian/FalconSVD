/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package falconsvd.controller;

import Jama.Matrix;
import falconsvd.model.FalconSVD;
import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;

/**
 * Esta es una clase controladora de eventos para el
 * elemento de menu 'Process -> FalconSVD -> Detection' de la clase JFrame
 * falconsvd.gui.FalconSVD.
 * 
 * @author sebaxtian
 * @version 1.0
 */


public class ControllerFalconDetection {
    
    public static double distance;

    public static void actionPerformed(ActionEvent e) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                registerProgress(10, "Inicia la deteccion del rostro");
                int kFaces = ControllerFalconMake.falconSVD.getMatrixTraining().rank(); // pueden ser dinamicos
                int norma = FalconSVD.NORMA2;
                String normaS = "Norma 2";
                if(falconsvd.gui.FalconSVD.buttonNorma1.isSelected()) {
                    norma = FalconSVD.NORMA1;
                    normaS = "Norma 1";
                }
                if(falconsvd.gui.FalconSVD.buttonNorma2.isSelected()) {
                    norma = FalconSVD.NORMA2;
                    normaS = "Norma 2";
                }
                if(falconsvd.gui.FalconSVD.buttonNormaFrob.isSelected()) {
                    norma = FalconSVD.NORMAFrob;
                    normaS = "Norma Frobenius";
                }
                if(falconsvd.gui.FalconSVD.buttonNormaInf.isSelected()) {
                    norma = FalconSVD.NORMAInf;
                    normaS = "Norma Infinita";
                }
                registerProgress(20, "Norma seleccionada: "+normaS);
                
                Matrix matrixTarget = ControllerOpenTarget.imageTarget.getReduceMatrix();
                double[] pixeles = matrixTarget.getRowPackedCopy();
                matrixTarget = new Matrix(pixeles, matrixTarget.getRowDimension()*matrixTarget.getColumnDimension());
                /*
                Matrix matrixTarget = ControllerOpenTarget.imageTarget.getReduceMatrix();
                double[] pixeles = matrixTarget.getColumnPackedCopy();
                matrixTarget = new Matrix(pixeles, matrixTarget.getRowDimension()*matrixTarget.getColumnDimension());
                */
                registerProgress(30, "Se obtiene con exito la imagen target");
                ControllerFalconMake.falconSVD.makeDetection(kFaces, norma, matrixTarget);
                registerProgress(70, "Se calcula la deteccion de la imagen");
                distance = ControllerFalconMake.falconSVD.getDistance();
                registerProgress(80, "Se obtiene el valor de coincidencia de la imagen "+distance);
                String message;
                if(distance <= ControllerEditThreshold.threshold) {
                    message = "La Imagen Objetivo Fue Encontrada, Con Un Valor De Coincidencia De: "+distance;
                } else {
                    message = "La Imagen Objetivo No Fue Encontrada, Con Un Valor De Coincidencia De: "+distance;
                }
                registerProgress(90, message);
                JOptionPane.showMessageDialog(falconsvd.gui.FalconSVD.panelDrawTarget, message, "Falcon Detection", JOptionPane.INFORMATION_MESSAGE);
                registerProgress(100, "La deteccion de la imagen target se ha completado");
            }
        };
        Thread hilo = new Thread(runnable);
        hilo.start();
    }
    
    /**
     * Metodo que registra en el area de log el progreso de la ejecucion
     * de reconocimiento de rostros mediante SVD y Eigenfaces, para el
     * caso de deteccion.
     * 
     * @param progress
     * @param message 
     */
    private static void registerProgress(final int progress, final String message) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(300);
                    falconsvd.gui.FalconSVD.textAreaLog.append("FalconDetection::FalconSVD [OK]\t "+message+"\n");
                    falconsvd.gui.FalconSVD.progressBar.setValue(progress);
                    falconsvd.gui.FalconSVD.progressBar.setString(progress+"%");
                    if(progress == 100) {
                        Thread.sleep(500);
                        falconsvd.gui.FalconSVD.progressBar.setValue(0);
                        falconsvd.gui.FalconSVD.progressBar.setString(0+"%");
                    }
                } catch (InterruptedException ex) {
                    falconsvd.gui.FalconSVD.textAreaLog.append("FalconDetection::FalconSVD [ERROR]\t Error al dormir hilo");
                }
            }
        };
        Thread hilo = new Thread(runnable);
        hilo.start();
    }
}
