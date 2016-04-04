// Jennifer Li <jli10@stanford.edu>
// Matching Game - A game of matching pairs of face-down cards.
// The user can restart the game anytime to play with a new randomized deck.

package cs193a.hw1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.Random;
import android.os.Handler;
import java.lang.Runnable;
import java.util.Arrays;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private Handler mHandler = new Handler();
    private int refresh_deck_delay = 100;
    private int show_match_delay = 500;

    private int children_before_deck = 3; // the welcome text, start button, and notification text
    private int num_cards = 12;

    private int card_click_count;
    private int prev_card_id;

    int[] CardFrontList; //contents of the cards, right now are ints, could change to something else
    int[] ButtonIdList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startButtonClick(View view) {
        //set to final so the delay function can access them
        //doesn't work when set to global variables
        final TextView notifier = (TextView) findViewById(R.id.notify);
        final RelativeLayout layout = (RelativeLayout)findViewById(R.id.layout);

        //initialize arrays
        CardFrontList = new int[num_cards];
        ButtonIdList = new int[num_cards];

        int start_id = view.getId();
        Button start_button = (Button) findViewById(start_id);


        start_button.setText("Restart");
        card_click_count = 0;
        prev_card_id = 0;

        notifier.setText("Good Luck!");

        for (int i = children_before_deck; i < layout.getChildCount(); i++) {
            View v = layout.getChildAt(i);
            v.setVisibility(View.GONE);
        }

        //set a delay so user knows the game restarted
        mHandler.postDelayed(new Runnable() {
            public void run() {
                //reshuffle deck and deal again
                shuffle_cards();

                for (int i = children_before_deck; i < (num_cards+children_before_deck); i++) {
                    Button v = (Button)layout.getChildAt(i);
                    v.setVisibility(View.VISIBLE);
                    v.setText("BACK");
                    int card_id = v.getId();
                    ButtonIdList[i-children_before_deck] = card_id;

                    //couldn't figure out log
                    //Log.d("myTag", String.valueOf(card_id));
                }
            }
        }, refresh_deck_delay);
    }

    public void cardClick(View view) {
        TextView notifier = (TextView) findViewById(R.id.notify);
        int card_id = view.getId();
        Button card_button = (Button) findViewById(card_id); //current button

        card_click_count++;

        //if card click count is odd, don't have to check it with another card, just display it
        if(card_click_count %2 == 1) {
            String specific_text = map_id_to_string(card_id); // each card has a specific front
            card_button.setText(specific_text);

            //also update previously clicked card id
            prev_card_id = card_id;
        }

        else{
            //check if the second click is the same card
            if(prev_card_id == card_id){
                //if second click is the same card, want to pretend it never happened
                //Don't flip it back over because otherwise the user can cheat >:(
                card_click_count --;
            }
            else {
                String specific_text = map_id_to_string(card_id);
                card_button.setText(specific_text);//display the card

                //if second card is a different card, check the front of both cards
                //set as final so the delay function can have access
                final Button check_current_card = (Button) findViewById(card_id);
                final Button check_prev_card = (Button) findViewById(prev_card_id);

                CharSequence string_current = check_current_card.getText();
                CharSequence string_prev = check_prev_card.getText();

                if(string_current == string_prev) {
                    notifier.setText("Wow Good Job!");
                    //delay here so users can see what the cards are
                    //unwanted behavior does occur if the user clicks on cards during the delay
                    //decided to assume user is not trying to break program...
                    mHandler.postDelayed(new Runnable() {
                        public void run() {
                            check_current_card.setVisibility(View.INVISIBLE);
                            check_prev_card.setVisibility(View.INVISIBLE);
                        }
                    }, show_match_delay);
                }

                //if no match, turn the cards back over
                else {
                    notifier.setText("Try Again");
                    mHandler.postDelayed(new Runnable() {
                        public void run() {
                            check_current_card.setText("BACK");
                            check_prev_card.setText("BACK");
                        }
                    }, show_match_delay);

                }

                //reset
                prev_card_id = 0;
                card_click_count = 0;

            }
        }
    }

    String map_id_to_string(int card_id){
        String mapped_string;

        //just make sure the way button ids are named make the arrays in sorted order
        //currently: button1, button2,etc
        //and set it up so that they are in order in the xml
        int index = Arrays.binarySearch(ButtonIdList, card_id);
        mapped_string = String.valueOf(CardFrontList[index]);
        return mapped_string;
    }


    void shuffle_cards(){
        //set up the array
        for(int i = 0; i<(num_cards);i+=2){
            CardFrontList[i] = i/2;
            CardFrontList[i+1] = i/2;
        }
        //shuffle the array
        //simple fisher yates shuffle (http://stackoverflow.com/questions/1519736/random-shuffling-of-an-array)
        Random randy = new Random();
        for (int i = num_cards - 1; i > 0; i--)
        {
            int index = randy.nextInt(i + 1);
            // Simple swap
            int a = CardFrontList[index];
            CardFrontList[index] = CardFrontList[i];
            CardFrontList[i] = a;
        }
    }
}

