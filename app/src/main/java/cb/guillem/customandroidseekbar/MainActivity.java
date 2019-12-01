package cb.guillem.customandroidseekbar;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    //--------------------
    // Static declarations
    //--------------------
    // Static seek bar range
    private final static int        SEEKBAR_MIN                         = 100;
    private final static int        SEEKBAR_MAX                         = 4250;

    // Static local Seek bar Alpha range
    private final static int        SEEKBAR_ALPHA_RANGE_MIN             = 75;
    private final static int        SEEKBAR_ALPHA_RANGE_MAX             = 255;

    // Static local Image Alpha range
    private final static int        IMAGE_ALPHA_RANGE_MIN               = 125;
    private final static int        IMAGE_ALPHA_RANGE_MAX               = 255;

    // Static animations duration
    private final static int        FADE_IN_DURATION_MS                 = 800;
    private final static int        FADE_OUT_DURATION_MS                = 500;

    // Static indicator layout background color
    private final static String     INDICATOR_LAYOUT_BACKGROUND_COLOR   = "#F0694E";

    // Member variable to store last given progress
    private int mLastProgress = 0;

    @Override @SuppressLint("NewApi")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //-----------------------
        // Fetching view pointers
        //-----------------------
        // Main view
        final RelativeLayout lMainLayout = findViewById(R.id.main_layout);
        // Seek bar
        final SeekBar lSeekbar = findViewById(R.id.seekbar);
        // Main view text
        final TextView lMainViewText = findViewById(R.id.mainText);
        // Indicator view
        final RelativeLayout lIndicatorLayout = findViewById(R.id.seekbarIndicatorLayout);
        // Indicator text
        final TextView lIndicatorText = lIndicatorLayout.findViewById(R.id.textIndicator);
        // Indicator image
        final ImageView lIndicatorImage = lIndicatorLayout.findViewById(R.id.indictorImage);

        //--------------------
        // Building animations
        //--------------------
        // Pair structure to buffer the animations
        final Pair<Animation,Animation> lAnimations = new Pair<>(
                AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in),
                AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out)
        );
        // Fade In Animation Local Pointer to 1st position
        final Animation FADE_IN = lAnimations.first;
        // Fade Out Animation Local Pointer to 2nd position
        final Animation FADE_OUT = lAnimations.second;
        // Setting Animation duration
        FADE_IN.setDuration(FADE_IN_DURATION_MS);
        FADE_OUT.setDuration(FADE_OUT_DURATION_MS);

        //---------------------
        // Setting the Seek Bar
        //---------------------
        // Setting Seek Bar range
        lSeekbar.setMin(SEEKBAR_MIN);
        lSeekbar.setMax(SEEKBAR_MAX);
        // Setting minimum value to the main view text
        lMainViewText.setText(String.format("Calories: %s", SEEKBAR_MIN));
        // Attaching seek bar's listener
        lSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            // Triggered when seek bar value changes
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Refresh the Indicator Layout text
                refreshLayoutsTexts(lIndicatorText, lMainViewText, progress);
                // Refresh the Indicator Layout image
                refreshImage(lIndicatorImage, seekBar);
                // Refresh the Main Layout alpha
                refreshLayoutAlpha(lMainLayout, seekBar);
            }

            // Triggered when user first touches the seek bar to start the drag move
            @Override public void onStartTrackingTouch(SeekBar seekBar) {
                // Refresh the main layout background's color
                resetBackgroundColor(lMainLayout, true);
                // Update text visibility
                setMainLayoutTextVisibility(lMainViewText, true);
                // Refresh the Indicator Layout text
                refreshLayoutsTexts(lIndicatorText, lMainViewText, seekBar.getProgress());
                // Refresh the Indicator Layout image
                refreshImage(lIndicatorImage, seekBar);
                // Refresh the Main Layout alpha
                refreshLayoutAlpha(lMainLayout, seekBar);

                // On Show Indicator Layout
                lIndicatorLayout.startAnimation(FADE_IN);
                lIndicatorLayout.setVisibility(View.VISIBLE);
            }

            // Triggered when user stops touching the seek bar
            @Override public void onStopTrackingTouch(SeekBar seekBar) {
                // Refresh the main layout background's color
                resetBackgroundColor(lMainLayout, false);
                // Update text visibility
                setMainLayoutTextVisibility(lMainViewText, false);
                // Refresh the Indicator Layout text
                refreshLayoutsTexts(lIndicatorText, lMainViewText, seekBar.getProgress());
                // Refresh the Indicator Layout image
                refreshImage(lIndicatorImage, seekBar);
                // Refresh the Main Layout alpha
                refreshLayoutAlpha(lMainLayout, seekBar);

                // On Hide Indicator Layout
                lIndicatorLayout.startAnimation(FADE_OUT);
                lIndicatorLayout.setVisibility(View.GONE);
            }
        });
    }

    /**
     * Refresh the TextView value given the current progress of the seek bar
     */
    private void refreshLayoutsTexts(TextView pIndicatorText, TextView pMainText, int pProgress){
        pIndicatorText.setText(String.format("%s", pProgress));
        pMainText.setText(String.format("Calories: %s", pProgress));
    }

    /**
     * Refresh the background color of the given Relative Layout.
     *      If it is the start the background is set to our desired color
     *      If it is not the start, background is reset to default white
     */
    private void resetBackgroundColor(RelativeLayout pRelative, boolean pIsStart){
        pRelative.setBackgroundColor(pIsStart ? Color.parseColor(INDICATOR_LAYOUT_BACKGROUND_COLOR) : Color.WHITE);
    }

    /**
     * Refresh the Text View Visibility regarding the current scenario
     */
    private void setMainLayoutTextVisibility(TextView pTextView, boolean pIsStart){
        pTextView.setVisibility(pIsStart ? View.GONE : View.VISIBLE);
    }

    /**
     * Refresh the background color of the given Relative Layout based on the Seek bar current value
     * Transform value from the Seek bar range to our local Alpha range and set´s the value
     */
    @SuppressLint({"NewApi", "LocalSuppress"})
    private void refreshLayoutAlpha(RelativeLayout pRelative, SeekBar pSeekbar){
        // Init values
        int                   X     = pSeekbar.getProgress();
        int                 MIN     = pSeekbar.getMin();
        int                 MAX     = pSeekbar.getMax();
        int     SEEKBAR_RANGE_MIN   = SEEKBAR_ALPHA_RANGE_MIN;
        int     SEEKBAR_RANGE_MAX   = SEEKBAR_ALPHA_RANGE_MAX;

        // Transform value from the Seek bar range to our Seek bar local Alpha range
        int     lBackgroundAlpha = (int) (((double)(X-MIN) / (double)(MAX-MIN)) * ( SEEKBAR_RANGE_MAX - SEEKBAR_RANGE_MIN ) + SEEKBAR_RANGE_MIN);

        // Buffer background alpha
        pRelative.getBackground().setAlpha(lBackgroundAlpha);
    }

    /**
     * Refresh the alpha of the given Image View based on the Seek bar current value
     * Transform value from the Seek bar range to our local Image Alpha range and set´s the value
     */
    @SuppressLint({"NewApi", "LocalSuppress"})
    private void refreshImage(ImageView pImageView, SeekBar pSeekbar){
        // Init values
        int                   X     = pSeekbar.getProgress();
        int                 MIN     = pSeekbar.getMin();
        int                 MAX     = pSeekbar.getMax();
        int     IMAGE_RANGE_MIN     = IMAGE_ALPHA_RANGE_MIN;
        int     IMAGE_RANGE_MAX     = IMAGE_ALPHA_RANGE_MAX;

        // Transform value from the Seek bar range to our Seek bar local Alpha range
        int     lImageAlpha = (int) (((double)(X-MIN) / (double)(MAX-MIN)) * ( IMAGE_RANGE_MAX - IMAGE_RANGE_MIN ) + IMAGE_RANGE_MIN);

        // Buffer image alpha
        pImageView.setAlpha(lImageAlpha);

        // Refresh image size regarding if it is increasing or decreasing
        if(X > mLastProgress){
            pImageView.getLayoutParams().height = pImageView.getLayoutParams().height + 1;
            pImageView.getLayoutParams().width = pImageView.getLayoutParams().width + 1;
        }else{
            pImageView.getLayoutParams().height = pImageView.getLayoutParams().height - 1;
            pImageView.getLayoutParams().width = pImageView.getLayoutParams().width - 1;
        }

        // Update last value
        mLastProgress = X;

        // Buffer new layout params
        pImageView.requestLayout();
    }
}
