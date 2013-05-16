package com.newproyectjmb;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.newproyectjmb.R;



public class MenuJuegoOLD extends Activity
{
	public static final int MODO_JUEGO_UN_PATO=1;
	public static final int MODO_JUEGO_DOS_PATOS=2;
    @Override
	protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.custom_view_1);
        setContentView(R.layout.layout_menu_juego);
        Button bjugarunpato = (Button) findViewById(R.id.un_pato);
        bjugarunpato.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("modojuego", MODO_JUEGO_UN_PATO);
                intent.setClass(MenuJuegoOLD.this, JuegoActivity.class);
                startActivity(intent);
                System.out.println("hola+menujuego");
                finish();
                System.out.println("adios+menujuego");
            }
        });
        Button bjugardospatos = (Button) findViewById(R.id.dos_patos);
        bjugardospatos.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("modojuego", MODO_JUEGO_DOS_PATOS);
                intent.setClass(MenuJuegoOLD.this, JuegoActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    @Override
	protected void onDestroy()
    {
    	super.onDestroy();
    	System.out.println("destruido+menujuego");
    }
}
