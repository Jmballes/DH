package com.newproyectjmb;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Contacts.People;
import android.provider.Contacts.Phones;
import android.telephony.gsm.SmsManager;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.newproyectjmb.R;


public class EnviarSMS extends ListActivity {
    private ListAdapter adaptador;
    String destinatario;
    int puntuacion;
    long idTelefono;
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
       	puntuacion = extras.getInt("puntuacion");


        Cursor c = getContentResolver().query(Phones.CONTENT_URI, null, null, null, null);
        startManagingCursor(c);

        adaptador = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_2, c, 
                        new String[] { Phones.NAME, Phones.NUMBER }, 
                        new int[] { android.R.id.text1, android.R.id.text2 });
        setListAdapter(adaptador);

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id){
    	super.onListItemClick(l, v, position, id);

    	Cursor c = (Cursor) adaptador.getItem(position);
    	idTelefono = c.getLong(c.getColumnIndex(People.NUMBER));
    	destinatario= c.getString(c.getColumnIndex(People.NAME));
    	showDialog(0);

    }

    protected Dialog onCreateDialog(int id) {
    	
            return new AlertDialog.Builder(EnviarSMS.this)

                .setMessage(getResources().getString(R.string.confirm_smstexto1)+" "+destinatario+". "+
                		getResources().getString(R.string.confirm_smstexto2))
                .setPositiveButton(R.string.dialog_salir_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    	SmsManager.getDefault().sendTextMessage(idTelefono+"", 
                                null, getResources().getString(R.string.smstexto1)+" "+destinatario+" "+
                        		getResources().getString(R.string.smstexto2)+" "+puntuacion+" "+
                        		getResources().getString(R.string.smstexto3), 
                                null, null); 

                    }
                })
                .setNegativeButton(R.string.dialog_salir_cancelar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        
                    }
                })
                .create();

            
    }
}