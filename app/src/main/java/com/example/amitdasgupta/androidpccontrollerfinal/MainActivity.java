package com.example.amitdasgupta.androidpccontrollerfinal;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
   static String SERVER_IP;
     static int SERVER_PORT;
    Context context;
    TextView tv;
    EditText ipget,portget;
    Button previous,pp,next;
    private boolean isConnected=false;
    private boolean mouseMoved=false;
    private Socket socket;
    //private PrintWriter out;
    private float initX=0;
    private float initY=0;
    private float disX=0;
    private float disY=0;
    public static DataOutputStream dos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=this;
        ipget=(EditText)findViewById(R.id.editText);
        portget=(EditText)findViewById(R.id.editText2);
        tv=(TextView)findViewById(R.id.textView);
        previous=(Button)findViewById(R.id.previous);
        pp=(Button)findViewById(R.id.pp);
        next=(Button)findViewById(R.id.next);
        tv.setBackgroundColor(Color.RED);


        pp.setOnClickListener(this);
        previous.setOnClickListener(this);
        next.setOnClickListener(this);
        tv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(isConnected&&dos!=null)
                {
                    switch (motionEvent.getAction())
                    {
                        case MotionEvent.ACTION_DOWN:
                            initX=motionEvent.getX();
                            initY=motionEvent.getY();
                            mouseMoved=false;
                            break;
                        case MotionEvent.ACTION_MOVE:
                            disX=((motionEvent.getX()-initX)*712)/1920;
                            disY=((motionEvent.getY()-initY)*764)/1080;
                            if(disX!=0||disY!=0)
                            {
                                //out.println(disX+","+disY);
                                try {
                                    dos.writeUTF(disX+","+disY);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            mouseMoved=true;
                            break;
                        case  MotionEvent.ACTION_UP:
                            if(!mouseMoved)
                            {
                                //out.println(Constants.MOUSE_LEFT_CLICK);
                                try {
                                    dos.writeUTF(Constants.MOUSE_LEFT_CLICK);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                    }
                }
                return  true;
            }
        });
    }

    @Override
    public void onClick(View view) {
         switch (view.getId())
         {
             case R.id.pp:
                 if(isConnected&&dos!=null)
                 {
                     //out.println(Constants.PLAY);
                     try {
                         dos.writeUTF(Constants.PLAY);
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
                 break;
             case R.id.next:
                 if (isConnected&&dos!=null)
                 {
                     //out.println(Constants.NEXT);
                     try {
                         dos.writeUTF(Constants.NEXT);
                     } catch (IOException e) {
                         e.printStackTrace();
                     }

                 }
                 break;
             case R.id.previous:
                 if (isConnected&&dos!=null)
                 {
                     //out.println(Constants.PREVIOUS);
                     try {
                         dos.writeUTF(Constants.PREVIOUS);
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
                 break;

         }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isConnected&&dos!=null)
        {
            try{
              //  out.println("exit");
                dos.writeUTF("exit");
                socket.close();
            } catch (IOException e) {
                Log.e("remotedroid","Error in closing socket",e);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.action_location_found)
        {    SERVER_IP=ipget.getText().toString();
            SERVER_PORT=Integer.parseInt(portget.getText().toString());
           // Toast.makeText(MainActivity.this,tv.getWidth()+" "+tv.getHeight(),Toast.LENGTH_LONG).show();
            ConnectPhoneTask connectPhoneTask=new ConnectPhoneTask();
            connectPhoneTask.execute(SERVER_IP);

            return  true;

        }
        else if (id==R.id.sharefiles)
            {

            }


        return super.onOptionsItemSelected(item);
    }


    public class ConnectPhoneTask extends AsyncTask<String,Void,Boolean>
    {

        @Override
        protected Boolean doInBackground(String... params) {
            boolean result = true;
            try {
                InetAddress serverAddr = InetAddress.getByName(params[0]);
                socket = new Socket(serverAddr, SERVER_PORT);//Open socket on server IP and port
            } catch (IOException e) {
                Log.e("remotedroid", "Error while connecting", e);
                result = false;

            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
           isConnected=aBoolean;
            Toast.makeText(context,isConnected?"Connected to server!":"Error while connecting to the server",Toast.LENGTH_LONG).show();
            try{
                if(isConnected)
                    dos=new DataOutputStream(socket.getOutputStream());
                   // out=new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
            } catch (IOException e) {
                Log.e(" remotedroid "," Error while creting outwriter ",e);
                Toast.makeText(context,"Error while connecting",Toast.LENGTH_LONG).show();
            }
        }
    }

}
