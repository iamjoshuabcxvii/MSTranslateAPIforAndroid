package com.example.joshu.translatorownimplementation;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.apache.commons.io.IOUtils;
import javax.net.ssl.HttpsURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;




public class MainActivity extends AppCompatActivity {

    //MS Translator Key is in here
    public static String key = "<MS Key is in here";
    private TextView txtTranslatedText, txtOriginalText;


    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtOriginalText = findViewById(R.id.txtOriginalText);
        txtTranslatedText = findViewById(R.id.txtTranslatedText);

//        btn = (Button) findViewById(R.id.btnTranslate);
//        // because we implement OnClickListener we only have to pass "this"
//        // (much easier)
//        btn.callOnClick();


    }



//    public void onClick(View view) {
//        // detect the view that was "clicked"
//        switch (view.getId()) {
//            case R.id.btnTranslate:
//                new LongOperation().execute("");
//                break;
//        }
//    }
//
//    private class LongOperation extends AsyncTask<String, Void, String> {
//
//        @Override
//        protected String doInBackground(String... params) {
////                    String textOrig = txtOriginalText.getText().toString();
//        String textOrig ="안녕하세요 123 -)^ 친구";
////        String textOrig;
//        textOrig=txtOriginalText.getText().toString();
//        String output;
//
//
//        output=getTranslation(textOrig);
//        txtTranslatedText.setText("Translated Text: "+output);
//            return "Executed";
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//
//            // might want to change "executed" for the returned string passed
//            // into onPostExecute() but that is upto you
//        }
//
//        @Override
//        protected void onPreExecute() {}
//
//        @Override
//        protected void onProgressUpdate(Void... values) {}
//    }








    public void translate(View view) {
//        String textOrig = txtOriginalText.getText().toString();
//        String textOrig ="안녕하세요 123 -)^ 친구";
        String textOrig;
        textOrig=txtOriginalText.getText().toString();
        String output;


        output=getTranslation(textOrig);
        txtTranslatedText.setText("Translated Text: "+output);


//        new LongOperation().execute("");




    }


    public static String getTranslation(String translatedTextStr) {

        try {
            // Get the access token
            // The key got from Azure portal, please see https://docs.microsoft.com/en-us/azure/cognitive-services/cognitive-services-apis-create-account
            String authenticationUrl = "https://api.cognitive.microsoft.com/sts/v1.0/issueToken";
            HttpsURLConnection authConn = (HttpsURLConnection) new URL(authenticationUrl).openConnection();
            authConn.setRequestMethod("POST");
            authConn.setDoOutput(true);
            authConn.setRequestProperty("Ocp-Apim-Subscription-Key", key);
            IOUtils.write("", authConn.getOutputStream(), "UTF-8");
            String token = IOUtils.toString(authConn.getInputStream(), "UTF-8");
//            System.out.println(token);  //Code to Display Token

//          Using the access token to build the appid for the request url
            String appId = URLEncoder.encode("Bearer " + token, "UTF-8");
            String text = URLEncoder.encode(translatedTextStr, "UTF-8");
            String from = "ko";
            String to = "en";
            String translatorTextApiUrl = String.format("https://api.microsofttranslator.com/v2/http.svc/GetTranslations?appid=%s&text=%s&from=%s&to=%s&maxTranslations=5", appId, text, from, to);
            HttpsURLConnection translateConn = (HttpsURLConnection) new URL(translatorTextApiUrl).openConnection();
            translateConn.setRequestMethod("POST");
            translateConn.setRequestProperty("Accept", "application/xml");
            translateConn.setRequestProperty("Content-Type", "text/xml");
            translateConn.setDoOutput(true);
            String TranslationOptions = "<TranslateOptions xmlns=\"http://schemas.datacontract.org/2004/07/Microsoft.MT.Web.Service.V2\">" +
                    "<Category>general</Category>" +
                    "<ContentType>text/plain</ContentType>" +
                    "<IncludeMultipleMTAlternatives>True</IncludeMultipleMTAlternatives>" +
                    "<ReservedFlags></ReservedFlags>" +
                    "<State>contact with each other</State>" +
                    "</TranslateOptions>";
            translateConn.setRequestProperty("TranslationOptions", TranslationOptions);
            IOUtils.write("", translateConn.getOutputStream(), "UTF-8");
            String resp = IOUtils.toString(translateConn.getInputStream(), "UTF-8");

            System.out.println(resp+"\n\n");
            String s=resp;
            Pattern assign_op=Pattern.compile("(<TranslatedText>)"
                    + "|(<\\/TranslatedText>)"
                    + "|[()\\\\[\\\\]{};=#.,'\\\\^:@!$%&_`*-<>]"
                    + "|[a-zA-Z0-9\\s]*"
                    + "");
            Matcher m = assign_op.matcher(s) ;

            String actualTranslation="";
            Boolean endOfTransTxt=false,startOfTransTxt=false,concat=false;
            String foundRegexStr="",tempStr="";

            while (m.find()) {
                foundRegexStr=m.group();

                if(m.group().matches("(<TranslatedText>)"))  {
                    startOfTransTxt=true;
                }
                else if(m.group().matches("(<\\/TranslatedText>)"))    {
                    endOfTransTxt=true;
                    concat=false;
                }
                else{
                    startOfTransTxt=false;
                    endOfTransTxt=false;
                }

                if(startOfTransTxt==true)  {
                    concat=true;
                }
                else if(concat==true) {
                    tempStr=tempStr+""+m.group();
                }
                else   {

                }
            }
//    System.out.println("\nTranslated Text:  "+tempStr);
            translatedTextStr=tempStr;


        } catch (Exception e) {
            e.printStackTrace();
        }

        return translatedTextStr;



    }


}




