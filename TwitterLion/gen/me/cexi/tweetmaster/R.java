/* AUTO-GENERATED FILE.  DO NOT MODIFY.
 *
 * This class was automatically generated by the
 * aapt tool from the resource data it found.  It
 * should not be modified by hand.
 */

package me.cexi.tweetmaster;

public final class R {
    public static final class attr {
        /** <p>May be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
<p>May be a color value, in the form of "<code>#<i>rgb</i></code>", "<code>#<i>argb</i></code>",
"<code>#<i>rrggbb</i></code>", or "<code>#<i>aarrggbb</i></code>".
         */
        public static final int adapterViewBackground=0x7f010000;
        /** <p>May be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
<p>May be a color value, in the form of "<code>#<i>rgb</i></code>", "<code>#<i>argb</i></code>",
"<code>#<i>rrggbb</i></code>", or "<code>#<i>aarrggbb</i></code>".
         */
        public static final int headerBackground=0x7f010001;
        /** <p>Must be a color value, in the form of "<code>#<i>rgb</i></code>", "<code>#<i>argb</i></code>",
"<code>#<i>rrggbb</i></code>", or "<code>#<i>aarrggbb</i></code>".
<p>This may also be a reference to a resource (in the form
"<code>@[<i>package</i>:]<i>type</i>:<i>name</i></code>") or
theme attribute (in the form
"<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>")
containing a value of this type.
         */
        public static final int headerTextColor=0x7f010002;
        /** <p>Must be one or more (separated by '|') of the following constant values.</p>
<table>
<colgroup align="left" />
<colgroup align="left" />
<colgroup align="left" />
<tr><th>Constant</th><th>Value</th><th>Description</th></tr>
<tr><td><code>pullDownFromTop</code></td><td>0x1</td><td></td></tr>
<tr><td><code>pullUpFromBottom</code></td><td>0x2</td><td></td></tr>
</table>
         */
        public static final int mode=0x7f010003;
    }
    public static final class color {
        public static final int color_black=0x7f060001;
        public static final int color_white=0x7f060000;
    }
    public static final class drawable {
        public static final int arrow=0x7f020000;
        public static final int backward=0x7f020001;
        public static final int forward=0x7f020002;
        public static final int ic_launcher=0x7f020003;
        public static final int layout_border=0x7f020004;
        public static final int login_btn=0x7f020005;
        public static final int pulltorefresh_down_arrow=0x7f020006;
        public static final int pulltorefresh_up_arrow=0x7f020007;
        public static final int refresh=0x7f020008;
    }
    public static final class id {
        public static final int backward=0x7f040017;
        public static final int button1=0x7f04000e;
        public static final int forward=0x7f040019;
        public static final int gridview=0x7f040002;
        public static final int linear_layout_fromtouser=0x7f04000a;
        public static final int linear_layout_link=0x7f04000c;
        public static final int pullDownFromTop=0x7f040000;
        public static final int pullUpFromBottom=0x7f040001;
        public static final int pull_refresh_list=0x7f04000f;
        public static final int pull_to_refresh_image=0x7f040012;
        public static final int pull_to_refresh_progress=0x7f040011;
        public static final int pull_to_refresh_text=0x7f040010;
        public static final int refresh=0x7f040018;
        public static final int scroll_view=0x7f040003;
        public static final int thumbImageDetail=0x7f040004;
        public static final int tweetTextView=0x7f040014;
        public static final int tweetUserTextView=0x7f040015;
        public static final int tweetUserThumbImageView=0x7f040013;
        public static final int txtDate=0x7f040007;
        public static final int txtExtraLink=0x7f04000d;
        public static final int txtFromAndToUser=0x7f04000b;
        public static final int txtName=0x7f040006;
        public static final int txtTweetText=0x7f040008;
        public static final int txtUserLink=0x7f040009;
        public static final int txtUserName=0x7f040005;
        public static final int user=0x7f04001b;
        public static final int webview=0x7f04001a;
        public static final int webview_top_menu=0x7f040016;
    }
    public static final class layout {
        public static final int detailtweetview=0x7f030000;
        public static final int main=0x7f030001;
        public static final int pull_to_refresh_header=0x7f030002;
        public static final int result_item=0x7f030003;
        public static final int webviewlayout=0x7f030004;
    }
    public static final class menu {
        public static final int menu=0x7f070000;
    }
    public static final class string {
        public static final int app_name=0x7f050004;
        public static final int app_title=0x7f050005;
        public static final int find=0x7f050007;
        public static final int no_results=0x7f050009;
        public static final int pull_to_refresh_pull_label=0x7f050000;
        public static final int pull_to_refresh_refreshing_label=0x7f050002;
        public static final int pull_to_refresh_release_label=0x7f050001;
        public static final int pull_to_refresh_tap_label=0x7f050003;
        public static final int results=0x7f050008;
        public static final int searching=0x7f05000a;
        public static final int str_tweets=0x7f050006;
        public static final int tweet_retrieval_failed=0x7f05000b;
        public static final int tweet_retrieval_failed_connection=0x7f05000c;
    }
    public static final class styleable {
        /** Attributes that can be used with a PullToRefresh.
           <p>Includes the following attributes:</p>
           <table>
           <colgroup align="left" />
           <colgroup align="left" />
           <tr><th>Attribute</th><th>Description</th></tr>
           <tr><td><code>{@link #PullToRefresh_adapterViewBackground me.cexi.tweetmaster:adapterViewBackground}</code></td><td></td></tr>
           <tr><td><code>{@link #PullToRefresh_headerBackground me.cexi.tweetmaster:headerBackground}</code></td><td></td></tr>
           <tr><td><code>{@link #PullToRefresh_headerTextColor me.cexi.tweetmaster:headerTextColor}</code></td><td></td></tr>
           <tr><td><code>{@link #PullToRefresh_mode me.cexi.tweetmaster:mode}</code></td><td></td></tr>
           </table>
           @see #PullToRefresh_adapterViewBackground
           @see #PullToRefresh_headerBackground
           @see #PullToRefresh_headerTextColor
           @see #PullToRefresh_mode
         */
        public static final int[] PullToRefresh = {
            0x7f010000, 0x7f010001, 0x7f010002, 0x7f010003
        };
        /**
          <p>This symbol is the offset where the {@link me.cexi.tweetmaster.R.attr#adapterViewBackground}
          attribute's value can be found in the {@link #PullToRefresh} array.


          <p>May be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
<p>May be a color value, in the form of "<code>#<i>rgb</i></code>", "<code>#<i>argb</i></code>",
"<code>#<i>rrggbb</i></code>", or "<code>#<i>aarrggbb</i></code>".
          @attr name me.cexi.tweetmaster:adapterViewBackground
        */
        public static final int PullToRefresh_adapterViewBackground = 0;
        /**
          <p>This symbol is the offset where the {@link me.cexi.tweetmaster.R.attr#headerBackground}
          attribute's value can be found in the {@link #PullToRefresh} array.


          <p>May be a reference to another resource, in the form "<code>@[+][<i>package</i>:]<i>type</i>:<i>name</i></code>"
or to a theme attribute in the form "<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>".
<p>May be a color value, in the form of "<code>#<i>rgb</i></code>", "<code>#<i>argb</i></code>",
"<code>#<i>rrggbb</i></code>", or "<code>#<i>aarrggbb</i></code>".
          @attr name me.cexi.tweetmaster:headerBackground
        */
        public static final int PullToRefresh_headerBackground = 1;
        /**
          <p>This symbol is the offset where the {@link me.cexi.tweetmaster.R.attr#headerTextColor}
          attribute's value can be found in the {@link #PullToRefresh} array.


          <p>Must be a color value, in the form of "<code>#<i>rgb</i></code>", "<code>#<i>argb</i></code>",
"<code>#<i>rrggbb</i></code>", or "<code>#<i>aarrggbb</i></code>".
<p>This may also be a reference to a resource (in the form
"<code>@[<i>package</i>:]<i>type</i>:<i>name</i></code>") or
theme attribute (in the form
"<code>?[<i>package</i>:][<i>type</i>:]<i>name</i></code>")
containing a value of this type.
          @attr name me.cexi.tweetmaster:headerTextColor
        */
        public static final int PullToRefresh_headerTextColor = 2;
        /**
          <p>This symbol is the offset where the {@link me.cexi.tweetmaster.R.attr#mode}
          attribute's value can be found in the {@link #PullToRefresh} array.


          <p>Must be one or more (separated by '|') of the following constant values.</p>
<table>
<colgroup align="left" />
<colgroup align="left" />
<colgroup align="left" />
<tr><th>Constant</th><th>Value</th><th>Description</th></tr>
<tr><td><code>pullDownFromTop</code></td><td>0x1</td><td></td></tr>
<tr><td><code>pullUpFromBottom</code></td><td>0x2</td><td></td></tr>
</table>
          @attr name me.cexi.tweetmaster:mode
        */
        public static final int PullToRefresh_mode = 3;
    };
}
