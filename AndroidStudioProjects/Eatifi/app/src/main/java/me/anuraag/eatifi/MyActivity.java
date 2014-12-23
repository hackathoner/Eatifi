package me.anuraag.eatifi;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class MyActivity extends Activity {
    private Button signin,login;
    private ParseUser myuser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

//        if (savedInstanceState == null) {
//            getFragmentManager().beginTransaction()
//                    .add(R.id.container, new SignUpFragment())
//                    .commit();
//        }
        Parse.initialize(this, "n2koKwQHYtQGedP92Uq6jEpHqMw7WByd6F11yMVh", "VObFmhueGhCXuNqeKZlkgeFkCB5Vw01gk1MkQNM9");
        ActionBar mybar = this.getActionBar();
        mybar.hide();
        final Intent myintent = new Intent(this,HomePage.class);
        myuser = ParseUser.getCurrentUser();

        if(myuser!= null){
            startActivity(myintent);
        }

        signin = (Button)findViewById(R.id.button2);
        login = (Button)findViewById(R.id.button);

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                signup();
                getFragmentManager().beginTransaction()
                        .add(R.id.container, new SignUpFragment())
                        .commit();
//                startActivity(myintent);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction()
                        .add(R.id.container, new LoginFragment())
                        .commit();
            }
        });

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public static class SignUpFragment extends Fragment {
        private Button submit;
        private EditText name,email,pass,repass;
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.signuplayout, container, false);

            return rootView;
        }
        public void onViewCreated(View view, Bundle savedInstanceState) {
            name = (EditText)view.findViewById(R.id.editText);
            email = (EditText)view.findViewById(R.id.editText2);
            pass = (EditText)view.findViewById(R.id.editText3);
            repass = (EditText)view.findViewById(R.id.editText4);

            submit = (Button)view.findViewById(R.id.button2);
            submit.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signup();
                }
            });

        }
        public void signup() {
            final Intent myintent = new Intent(getActivity(),HomePage.class);
            if (!name.getText().toString().isEmpty() && !email.getText().toString().isEmpty() && !pass.getText().toString().isEmpty()  && !repass.getText().toString().isEmpty() && pass.getText().toString().equals(repass.getText().toString()) && pass.getText().toString().length() >= 6)
            {
                ParseUser user = new ParseUser();
                user.setUsername(email.getText().toString());
                user.setPassword(pass.getText().toString());
                user.setEmail(email.getText().toString());
                user.put("Name",name.getText().toString());

                user.signUpInBackground(new SignUpCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            showSuccess("Please verify your email, then click the login button");
                        } else {
                            showIssue(e.toString());
                        }
                    }
                });
            }else if (name.getText().toString().isEmpty() || email.getText().toString().isEmpty() || pass.getText().toString().isEmpty()  || repass.getText().toString().isEmpty()){
                showIssue("Please makes sure you fill out all the fields properly!");
            }else if(!pass.getText().toString().equals(repass.getText().toString())){
                showIssue("Oops your passwords do not match, please try again");
            }else if(pass.getText().toString().length()<6){
                showIssue("Please make sure your password is at least six characters long");
            }
        }
        private void showSuccess(String issue)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(issue)
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            getActivity().finish();
                            startActivity(getActivity().getIntent());
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
        private void showIssue(String issue)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(issue)
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

    }
    public static class LoginFragment extends Fragment {
        private Button submit;
        private ParseUser myuser;
        private EditText name,email,pass,repass;
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.loginlayout, container, false);

            return rootView;
        }
        public void onViewCreated(View view, Bundle savedInstanceState) {
            email = (EditText)view.findViewById(R.id.editText2);
            pass = (EditText)view.findViewById(R.id.editText3);

            submit = (Button)view.findViewById(R.id.button2);
            submit.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    login();
                }
            });

        }
        public void login(){
            final Intent myintent = new Intent(getActivity(),HomePage.class);

            ParseUser.logInInBackground(email.getText().toString(), pass.getText().toString(), new LogInCallback() {
                public void done(ParseUser user, ParseException e) {
                    if (user != null) {
                        Log.i("User",user.getEmail());
                        boolean worked = user.getBoolean("emailVerified");
                        if(!worked) {
                            showIssue("Please make sure you have verified your email");

                        }else {
                            myuser = ParseUser.getCurrentUser();
                            startActivity(myintent);
                        }
                    } else {

                        showIssue(e.toString());


                    }
                }
            });
        }
        private void showSuccess(String issue)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(issue)
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            getActivity().finish();
                            startActivity(getActivity().getIntent());
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
        private void showIssue(String issue)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(issue)
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

    }
}
