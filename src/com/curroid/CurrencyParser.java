package com.curroid;

import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import android.widget.Toast;

import android.app.Activity;

//stworzenie bazowego EventHandlera dla SAX 
public class  CurrencyParser extends DefaultHandler
{
    private String pubDate = ""; //string przechowuj¹cy informacjê, z jakiego dnia jest dane notowanie
    public ArrayList<String> currencyCode = new ArrayList<String>(); //arraylist, przechowuj¹cy kody walut w postaci stringów
    private ArrayList<Float> currencyValue = new ArrayList<Float>(); //arraylist, przechowuj¹cy kursy walut w postaci floata
    private String src = "http://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml"; //src danych
    
    public void setPubDate(String pubDate) {
                this.pubDate = pubDate;
        }

        public String getPubDate() {
                return pubDate;
        }
    public CurrencyParser(Activity a) {
        XMLReader xmlReader = null;
                try {
                		//stworz nowa instancje automatyzujaca tworzenie obiektu
                		//klasy SAXParser
                        SAXParserFactory spfactory = SAXParserFactory.newInstance();
                        //na wszelki wypadek, wylaczamy walidacje XML :)
                        spfactory.setValidating(false);
                        //stworz nowy obiekt klasy SAXParser i podlacz jego xmlReader
                        //do glownego xmlReadera klasy
                        SAXParser saxParser = spfactory.newSAXParser();
                        xmlReader = saxParser.getXMLReader();
                        //ustaw biezaca klase jako domyslny handler 
                        //dla parsowanego contentu i dla bledow
                        xmlReader.setContentHandler(this);
                        xmlReader.setErrorHandler(this);
                        //podlacz sie do zrodla danych
                        InputSource source = new InputSource(src);
                        //parsuj plik z danymi, wysylajac kazde zdarzenie
                        //do odpowiedniego handlera
                        xmlReader.parse(source);
                } catch (Exception e) {
                        Toast.makeText(a, "No internet connection", 10).show();
                }
    }
    
    //funkcja do pobierania kursu, na podstawie kodu waluty
        public float getCurcByCode(String currCode) {
                int ind = 0;
                if (currCode.equalsIgnoreCase("EUR")) return 1;
                if (!(currencyCode.contains(currCode))) {
                        return 0;
                } else {
                        ind = currencyCode.indexOf(currCode);
                        return currencyValue.get(ind);
                }
        }
        //przeliczanie stosunku curr1 do curr2
        public float getRelativeExchange(String curr1, String curr2){
                
                float c1, c2, c3;
                if (curr1.equalsIgnoreCase("EUR")) c1=1; else c1 = getCurcByCode(curr1);
                if (curr2.equalsIgnoreCase("EUR")) c2=1; else c2 = getCurcByCode(curr2);
                if(c1!=0) c3 = c2/c1; else c3=0;
                return c3;
        }
        
   //funkcja startElement odpowiada za zdarzenia, które maj¹ miejsce przy otwarciu tagu XML
        @Override
        public void startElement(String namespaceURI, String localName,
                        String qName, Attributes atts) throws SAXException {

                // jezeli natrafimy na tag, o nazwie Cube
                if (qName.equalsIgnoreCase("Cube")){
                //pobieramy jego odpowiednie elementy do list
                for (int att = 0; att < atts.getLength(); att++) {
                       String attName = atts.getQName(att);
                       if(attName.equalsIgnoreCase("currency"))
                           currencyCode.add(atts.getValue(attName));
                       if(attName.equalsIgnoreCase("rate")){
                           float f1 = Float.parseFloat(atts.getValue(attName));
                           currencyValue.add(f1);
                       }
                       if(attName.equalsIgnoreCase("time"))
                           setPubDate(atts.getValue(attName));  
                   }
                }
                
        }   
}
