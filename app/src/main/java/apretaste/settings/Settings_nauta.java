package apretaste.settings;

import android.content.DialogInterface;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import apretaste.activity.MainActivity;
import apretaste.ProfileInfo;
import com.example.apretaste.R;
import apretaste.email.Mailer;
import apretaste.email.Mailerlistener;
import apretaste.Helper.PrefsManager;
import com.google.gson.Gson;

public class Settings_nauta extends AppCompatActivity implements Mailerlistener {
    public static final String SIN_SEGURIDAD = "Sin seguridad";
    public static final String SMTP_SSL = "smtp_ssl";
    public static final String SMTP_PORT = "smtp_port";
    public static final String SMTP_NAUTA_CU = "smtp.nauta.cu";
    public static final String SMTP_SERVER = "smtp_server";
    public static final String IMAP_SSL = "imap_ssl";
    public static final String IMAP_PORT = "imap_port";
    public static final String IMAP_NAUTA_CU = "imap.nauta.cu";
    public static final String IMAP_SERVER = "imap_server";
    public static final String PASS = "pass";
    public static final String USER = "user";
    public static final String OPCIONES_DE_SEGURIDAD = "Opciones de seguridad";
    public static final String SSL = "SSL";
    PrefsManager prefsManager = new PrefsManager();
    EditText et_email,et_password,et_server_smtp,et_security_smtp,et_server_imap,et_security_imap,et_port_smtp,et_port_imap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_nauta);


        et_email = (EditText) findViewById(R.id.et_settings_email);
        et_password = (EditText) findViewById(R.id.et_settings_password);
        et_server_imap = (EditText) findViewById(R.id.et_settings_server_imap);
        et_port_imap = (EditText) findViewById(R.id.et_settings_port_imap);
        et_server_smtp = (EditText) findViewById(R.id.et_settings_server_smtp);
        et_port_smtp = (EditText) findViewById(R.id.et_settings_port_smtp);
        et_security_smtp = (EditText) findViewById(R.id.et_settings_security_smtp);
        et_security_imap = (EditText) findViewById(R.id.et_settings_security_imap);

        final CharSequence[] sslOptions=new CharSequence[]{SSL, SIN_SEGURIDAD};
        View.OnClickListener sslListener=new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                new AlertDialog.Builder(Settings_nauta.this).setItems(sslOptions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((EditText)v).setText(sslOptions[which]);
                    }
                }).setTitle(OPCIONES_DE_SEGURIDAD).show();
            }
        };
        et_security_smtp.setOnClickListener(sslListener);
        et_security_imap.setOnClickListener(sslListener);

        prefsManager.show_value(this,et_email,USER,"");
        prefsManager.show_value(this,et_password,PASS,"");

        prefsManager.show_value(this,et_server_imap,IMAP_SERVER,IMAP_NAUTA_CU);
        prefsManager.show_value(this,et_port_imap,IMAP_PORT,"143");
        prefsManager.show_value(this,et_security_imap,IMAP_SSL,SIN_SEGURIDAD);

        prefsManager.show_value(this,et_server_smtp,SMTP_SERVER,SMTP_NAUTA_CU);
        prefsManager.show_value(this,et_port_smtp,SMTP_PORT,"25");
        prefsManager.show_value(this,et_security_smtp,SMTP_SSL,SIN_SEGURIDAD);

        findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (    et_email.getText().toString().isEmpty() ||
                        et_password.getText().toString().isEmpty() ||

                        et_server_imap.getText().toString().isEmpty() ||
                        et_port_imap.getText().toString().isEmpty() ||
                        et_security_imap.getText().toString().isEmpty() ||

                        et_server_smtp.getText().toString().isEmpty() ||
                        et_port_smtp.getText().toString().isEmpty() ||
                        et_security_smtp.getText().toString().isEmpty()){
                    Toast.makeText(getBaseContext(),"Rellene todos los campos",Toast.LENGTH_SHORT).show();
                }else{
                    Mailer mailer=new Mailer(Settings_nauta.this, null, MainActivity.PERFIL_STATUS + MainActivity.pro.timestamp, false, null, Settings_nauta.this);
                    mailer.setReturnContent(true).setSaveInternal(true);
                    // mailer.setCustomText(ESTAMOS_BUSCANDO_NUEVO_SERVICIOS_CHATS_NOTIFICACIONES_Y_CAMBIOS_EN_SU_PERFIL_POR_FAVOR_SEA_PACIENTE_Y_NO_CIERRE_LA_APLICACION);
                    mailer.setShowCommand(false);
                    mailer.setAppendPassword(true);
                    //mailer.setShowStatus(false);
                    mailer.execute();
                }
            }
        });



    }

    @Override
    public void onMailSent() {

    }

    @Override
    public void onError(Exception e) {

    }

    @Override
    public void onResponseArrived(String service, String command, String response, Mailer mailer) {
        if(mailer.getReturnContent())
        {
            Log.e("pro","retcontent");
            ProfileInfo pinfo;
            try
            {
                pinfo =new Gson().fromJson(response,ProfileInfo.class);
                Log.e("pro","pinfo jsoned");
            }
            catch (Exception e)
            {
                Toast.makeText(this, "Ha ocurrido un error en el servidor.", Toast.LENGTH_SHORT).show();
                Log.e("pro","error jsoning pinfo");
                return;
            }
            Log.e("pro","updating");
            MainActivity.pro.update(pinfo);
            Log.e("pro","jsoning");
            String prof=new Gson().toJson( MainActivity.pro);
            Log.e("pro","saving");
            PreferenceManager.getDefaultSharedPreferences(Settings_nauta.this).edit().putString(MainActivity.RESP,prof).apply();
            MainActivity.needsReload=true;
            MainActivity.needsreloadServices=true;
            prefsManager.change_value(Settings_nauta.this ,"user" , et_email);
            prefsManager.change_value(Settings_nauta.this ,"pass" , et_password);
            //Imap
            prefsManager.change_value(Settings_nauta.this ,"imap_server" , et_server_imap);
            prefsManager.change_value(Settings_nauta.this ,"imap_port" , et_port_imap);
            prefsManager.change_value(Settings_nauta.this ,"imap_ssl" , et_security_imap);

            //Smtp
            prefsManager.change_value(Settings_nauta.this ,"smtp_server" , et_server_smtp);
            prefsManager.change_value(Settings_nauta.this ,"smtp_port" , et_port_smtp);
            prefsManager.change_value(Settings_nauta.this ,"smtp_ssl" , et_security_smtp);

            Toast.makeText(getBaseContext(),"Se han guardado los datos",Toast.LENGTH_SHORT).show();
            return;
        }
    }
}
