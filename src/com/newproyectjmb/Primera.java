package com.newproyectjmb;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.newproyectjmb.R;

public class Primera extends Activity {
    /** Called when the activity is first created. */
	//public static final int MENU_JUGAR = 0;
	//public static final int MENU_OPCIONES = 1;

	public static final int DIALOG_AYUDA = 3;
	public static final int DIALOG_SALIR = 4;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_principal);
        Button bjugar = (Button) findViewById(R.id.jugar);

        bjugar.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("modojuego", 1);
				intent.setClass(Primera.this, JuegoActivity.class);
                startActivity(intent);
            	
            }
        });
        Button bAbout = (Button) findViewById(R.id.acercade);
        bAbout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(Primera.this, About.class);
                startActivity(intent);
            }
        });
        
        Button bexit = (Button) findViewById(R.id.salir);
        bexit.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                showDialog(DIALOG_SALIR);
            }
        });
        Button bayuda = (Button) findViewById(R.id.ayuda);
        bayuda.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                showDialog(DIALOG_AYUDA);
            }
        });
        Button boptions = (Button) findViewById(R.id.opciones);
        boptions.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Primera.this, Opciones.class);
                startActivity(intent);
            }
        });
        
    }
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DIALOG_SALIR:
            return new AlertDialog.Builder(Primera.this)
                .setIcon(R.drawable.uah_icon)
                .setTitle(R.string.dialog_salir_titulo)
                .setMessage(R.string.dialog_salir_texto)
                .setPositiveButton(R.string.dialog_salir_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    	finish();
                        /* User clicked OK so do some stuff */
                    }
                })
                .setNegativeButton(R.string.dialog_salir_cancelar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        /* User clicked Cancel so do some stuff */
                    }
                })
                .create();
        case DIALOG_AYUDA:
        	LayoutInflater factory = LayoutInflater.from(this);
            final View textEntryView = factory.inflate(R.layout.layout_ayuda, null);
            return new AlertDialog.Builder(Primera.this)
            .setIcon(R.drawable.uah_icon)
            .setTitle(R.string.ayuda)
            .setView(textEntryView)
//            .setPositiveButton(R.string.dialog_salir_ok, new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int whichButton) {
//
//                    /* User clicked OK so do some stuff */
//                }
//            })
//            .setNegativeButton(R.string.dialog_salir_cancelar, new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int whichButton) {
//
//                    /* User clicked cancel so do some stuff */
//                }
//            })
            .create();

        }

        	
        return null;
    }
}