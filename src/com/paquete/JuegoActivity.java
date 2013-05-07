package com.paquete;



import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Spinner;

import com.newproyectjmb.R;
import com.paquete.JuegoView.JuegoThread;



/**
 * This is a simple LunarLander activity that houses a single LunarView. It
 * demonstrates...
 * <ul>
 * <li>animating by calling invalidate() from draw()
 * <li>loading and drawing resources
 * <li>handling onPause() in an animation
 * </ul>
 */
public class JuegoActivity extends Activity {


    /** Manejador de nuestro Thread. */
    private JuegoThread mJuegoThread;

    /** El objeto View en el que el juego se ejecuta*/
    private JuegoView mJuegoView;


    private int modeJuego=-1;
    
    SensorManager mSensorManager;


    /**
     * Invocado cuando la Activity es creada
     * 
     * @param savedInstanceState un Bundle contiene un estado guardado de una 
     *        ejecución previa, o null si es una ejecución nueva
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Obtenemos la información extra de nuestro Intent
        Bundle extras = getIntent().getExtras();
        //Nos centramos en el campo modo de juego
       	modeJuego = extras.getInt("modojuego");
       	//Establecemos nuestro layout
        setContentView(R.layout.layout_juego);
        //Obtenemos el identificador de nuestro elemento principal (y unico) de nuestro layout
        mJuegoView = (JuegoView) findViewById(R.id.juego);

        //Establecemos el modo de juego
        mJuegoView.setTotalDePatosPorMiniFase(modeJuego);
        //Obtenemos el hilo de nuestro juego
        mJuegoThread = mJuegoView.getThread();
        mJuegoThread.setActivity(this);
        System.out.println("Activty create");
        //Solicitamos un manejador del sensor de nuestro dispositivo.
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        

    }
    public final Handler mHandler = new Handler() {
        @Override public void handleMessage(Message msg) {
            switch (msg.what) {
                
                // It is time to bump the value!
                case 0: {
                	showDialog(3);
                } break;
                default:
                	
                    super.handleMessage(msg);
            }
            System.out.println("Enviando mensajes");
        }
    };
    @Override
    protected void onStop() {

    	super.onStop();
    	//Dejamos de obtener eventos de nuestro sensor
    	mSensorManager.unregisterListener(mJuegoView);
    	//Paramos cualquier sonido en caso de que se este reproducciendo
    	mJuegoThread.stopSound();
    }
    protected void onDestroy(){
    	
    	super.onDestroy();
    	mSensorManager.unregisterListener(mJuegoView);
    }
    @Override
    protected void onResume(){
    	super.onResume();
    	//Solicitamos obtener eventos de nuestro sensor
        mSensorManager.registerListener(mJuegoView,
                SensorManager.SENSOR_ORIENTATION,
                SensorManager.SENSOR_DELAY_FASTEST);
    }
    @Override
    protected void onPause(){
    	super.onPause();
    	//Dejamos de obtener eventos de nuestro sensor
        mSensorManager.unregisterListener(mJuegoView);
    }
    private static final int MENU_CONTINUAR = 1;
    private static final int MENU_OPCIONES = 2;
    private static final int MENU_SALIR = 3;

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, MENU_CONTINUAR, 0, R.string.menu_continuar);
        menu.add(0, MENU_OPCIONES, 0, R.string.menu_opciones);
        menu.add(0, MENU_SALIR, 0, R.string.menu_salir);

        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu){
    	mJuegoThread.estableceEstadoPausa();
    	return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_CONTINUAR:
            	mJuegoThread.vuelveAlJuegoTrasLaPausa();
                return true;
            case MENU_SALIR:
                finish();
                return true;
            case MENU_OPCIONES:
            	showDialog(1);
                return true;

        }
        return false;
    }
    

	private CheckBox sonido;
	private CheckBox vibracion;
	private Spinner modo;
	SharedPreferences app_preferences;


    protected Dialog onCreateDialog(int id) {
        switch (id) {

        case 3:
        	return new AlertDialog.Builder(JuegoActivity.this)
            .setIcon(R.drawable.uah_icon)
            .setTitle(R.string.dialog_sms_titulo)
            .setMessage(getResources().getString(R.string.gameover_question))
            .setPositiveButton(R.string.dialog_salir_ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
		            Intent intent = new Intent();
		            intent.putExtra("puntuacion", mJuegoThread.getScore());
		            intent.setClass(JuegoActivity.this, EnviarSMS.class);
		            startActivity(intent);
		            finish();
		            
                }
            })
            .setNegativeButton(R.string.dialog_salir_cancelar, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	finish();
                }
            })
            .create();

        case 1:
        	//Lectura de preferencias guardadas
        	app_preferences = PreferenceManager.getDefaultSharedPreferences(this); 
            boolean sonido_boolean = app_preferences.getBoolean("preference_sonido", false);
            boolean vibracion_boolean = app_preferences.getBoolean("preference_vibracion", false);
            String control = app_preferences.getString("preference_controles", "no valor");
            
            //Conseguimos los identificadores de nuestra interfaz guardada en XML
            LayoutInflater factory = LayoutInflater.from(this);
            final View textEntryView = factory.inflate(R.layout.layout_opciones_pausa, null);
            sonido=(CheckBox)textEntryView.findViewById(R.id.check2);
            vibracion=(CheckBox)textEntryView.findViewById(R.id.check3);
            modo=(Spinner)textEntryView.findViewById(R.id.controles);
            
            //Establecemos las preferencias cargadas, a nuestra interfaz
            this.sonido.setChecked(sonido_boolean);
            this.vibracion.setChecked(vibracion_boolean);
            this.modo.setSelection(control.equals("tm")?0:1);
            
            //Creamos el dialogo
            return new AlertDialog.Builder(this)
            .setIcon(R.drawable.uah_icon)
            .setTitle(R.string.opciones)
            .setView(textEntryView)
            .setPositiveButton(R.string.dialog_salir_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    	SharedPreferences.Editor editor = app_preferences.edit();
                        editor.putBoolean("preference_sonido", sonido.isChecked());
                        editor.putBoolean("preference_vibracion", vibracion.isChecked());
                        editor.putString("preference_controles", modo.getSelectedItemId()==0?"tm":"mm");
                        editor.commit();
                        mJuegoView.readPreferences();

                    }
             })
            .setNegativeButton(R.string.dialog_salir_cancelar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
            })
            .create();
        }
        return null;
    }




}