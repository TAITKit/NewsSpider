//Title:        PatTree Matching
//Version:
//Copyright:    Copyright (c) 1999
//Author:       Baker
//Company:      AiiA
//Description:  HTMLTokenizer form Java_Book

import java.io.*;
import java.util.*;

class HTMLTokenizer extends StreamTokenizer
{
   int  	m_seat;
   int  	m_encode=0;
   boolean 	outsideTag = true;
   String 	m_htmltag;
   String 	m_strText;


   //---------------------------------------------------------------------
   // The HTMLTokenizer relies on a two-state state machine: the stream
   // can be "inside" a tag (between < and >) or "outside" a tag
   // (between > and <)/
   //---------------------------------------------------------------------
   public HTMLTokenizer(Reader reader) {
      super(reader);

      resetSyntax();		    // start with a blank character type table
      wordChars(0, 255);	    // you want to stumble over < and > only,
      ordinaryChars('<', '<');	// all the rest is considered "words"
      ordinaryChars('>', '>');

      outsideTag = true;	    // you start being outside any HTML tags
	  m_htmltag = new String();
	  m_strText = new String();
   }

   public void set_encode_option(int code)
   {
     m_encode=code;
   }

   //--------------------------------------------------------------------
   // grab next HTML tag, text, or EOF
   //--------------------------------------------------------------------
   public int nextHTML() throws IOException {
      int tok;

      switch(tok = nextToken()) {
	  case StreamTokenizer.TT_EOF:
	    return HTML_EOF;
	  case '<':
	    outsideTag = false; // we're inside
	    return nextHTML();  // decode type
	  case '>':
	    outsideTag = true;
	    return nextHTML();
	  case StreamTokenizer.TT_WORD:
	    if (!outsideTag) {            
	        return tagType(); //decode tag type
	    }else {
			if (onlyWhiteSpace(sval)) {
				return nextHTML();
			} else {
                //m_strText=sval;
                return HTML_TEXT;
			}
	    }
	  default:
	    System.out.println("ERROR: unknown TT " + tok);
      }
      return HTML_UNKNOWN;
   }

   //-----------------------------------------------------------------------
   // Inter-tag words that consist only of whitespace are swallowed
   // (skipped); this method tests whether a string can be considered
   // whitespace or not
   //-----------------------------------------------------------------------

	protected boolean onlyWhiteSpace(String s) {
      char ch;
      StringTokenizer st;
      String str;
      StringBuffer buff=new StringBuffer();

      s.trim();
      st = new StringTokenizer(s);
      m_seat= Integer.parseInt(st.nextToken());

      while(st.hasMoreTokens())
      {
          str=st.nextToken();
          for(int i=0;i<str.length();i++)
          {   ch = str.charAt(i);
              if (!(ch == '\t' || ch == '\n' || ch == '\r'))
                  return false;
          }
          buff.append(str);
      }
      m_strText=buff.toString();
      return true;
	}

   //-----------------------------------------------------------------------
   // You've just hit a '<' tag start character; now identify the type
   // of tag you're dealing with.
   //-----------------------------------------------------------------------
	protected int tagType() {
		boolean endTag = false;
		String input;
		String block;
		int start = 0;
		int tagID;
		StringTokenizer st;

		input = sval;
        //Removes white space from both ends of this string
		input.trim();
        input.toUpperCase();
		st = new StringTokenizer(input);

		//m_seat 儲存TAG在html_file的位置
		m_seat= Integer.parseInt(st.nextToken());
		//m_htmltag 儲存TAG字串token
		try{
			m_htmltag=st.nextToken();
		}
		catch(Exception e)
		{	
			return HTML_UNKNOWN;
		}
		
		if (m_htmltag.charAt(0) == '/') // is this an end tag (like </HTML>)?
        {	start++;                    // skip slash
			endTag = true;
		}
		// go through the list og known tags, try to match one
		for (int tag = 0; tag < tags.length; tag++) {
			if(m_htmltag.regionMatches(true, start, tags[tag],0, tags[tag].length()))
			{	tagID = tagcode[tag][m_encode] + (endTag ? 1 : 0);
				return tagID;
			}
		}
		return HTML_UNKNOWN;
	}

    public int tagNumber()
    {  	boolean endTag = false;
		int start = 0;
		int tagID;
		StringBuffer input=new StringBuffer(sval);
        if (input.charAt(0) == '/') // is this an end tag (like </HTML>)?
        {	start++;                // skip slash
			endTag = true;
            input=input.deleteCharAt(0);
		}
		// go through the list og known tags, try to match one
		for (int tag = 0; tag < tags.length; tag++){
			if(input.toString().equalsIgnoreCase(tags[tag]))
			{   tagID = tagcode[tag][m_encode] + (endTag ? 1 : 0);
				return tagID;
			}
		}
		return HTML_UNKNOWN;
    }


    //編碼
    static int HTML_TEXT = -1;
    static int HTML_UNKNOWN = -2;
    static int HTML_EOF = -7;

    // The following class constants are used to identify HTML tags.
    // Note that each tag type has an odd- and even-numbered ID,
    // depending on whether the tag is a start or end tag.
    // These constants are returned by nextHTML().
    static int TAG_ADDRESS=68,    TAG_address=69 ;
    static int TAG_APPLET=148,    TAG_applet=149 ;
    static int TAG_AREA=210,      TAG_area=211 ;
    static int TAG_A=98,          TAG_a=99 ;
    static int TAG_BASEFONT=212,  TAG_basefont=213 ;
    static int TAG_BASE=136,      TAG_base=137 ;
    static int TAG_BIG=118,       TAG_big=119 ;
    static int TAG_BLOCKQUOTE=66, TAG_blockquote=67 ;
    static int TAG_BODY=132,      TAG_body=133 ;
    static int TAG_BR=112,        TAG_br=113 ;
    static int TAG_B=102,         TAG_b=103 ;
    static int TAG_CAPTION=204,   TAG_caption=205 ;
    static int TAG_CENTER=72,     TAG_center=73 ;
    static int TAG_CITE=76,       TAG_cite=77 ;
    static int TAG_CODE=114,      TAG_code=115 ;
    static int TAG_DD=60,         TAG_dd=61 ;
    static int TAG_DFN=122,       TAG_dfn=123 ;
    static int TAG_DIR=50,        TAG_dir=51 ;
    static int TAG_DIV=70,        TAG_div=71 ;
    static int TAG_DL=56,         TAG_dl=57 ;
    static int TAG_DT=58,         TAG_dt=59 ;
    static int TAG_EM=108,        TAG_em=109 ;
    static int TAG_FONT=100,      TAG_font=101 ;
    static int TAG_FORM=176,      TAG_form=177 ;
    static int TAG_FRAMESET=156,  TAG_frameset=157 ;
    static int TAG_FRAME=152,     TAG_frame=153 ;
    static int TAG_H1=34,         TAG_hQ=35 ;
    static int TAG_H2=38,         TAG_hR=39 ;
    static int TAG_H3=36,         TAG_hS=37 ;
    static int TAG_H4=40,         TAG_hT=41 ;
    static int TAG_H5=42,         TAG_hU=43 ;
    static int TAG_H6=94,         TAG_hV=95 ;
    static int TAG_HEAD=130,      TAG_head=131 ;
    static int TAG_HR=118,        TAG_hr=119 ;
    static int TAG_HTML=128,      TAG_html=129 ;
    static int TAG_IFRAME=158,    TAG_iframe=159 ;
    static int TAG_IMG=96,        TAG_img=97 ;
    static int TAG_INPUT=178,     TAG_input=179 ;
    static int TAG_ISINDEX=186,   TAG_isindex=187 ;
    static int TAG_I=104,         TAG_i=105 ;
    static int TAG_KBD=116,       TAG_kbd=117 ;
    static int TAG_LINK=142,      TAG_link=143 ;
    static int TAG_LI=54,         TAG_li=55 ;
    static int TAG_MAP=208,       TAG_map=209 ;
    static int TAG_MENU=52,       TAG_menu=53 ;
    static int TAG_META=138,      TAG_meta=139 ;
    static int TAG_OL=48,         TAG_ol=49 ;
    static int TAG_OPTION=182,    TAG_option=183 ;
    static int TAG_PARAM=150,     TAG_param=151 ;
    static int TAG_PRE=64,        TAG_pre=65 ;
    static int TAG_P=62,          TAG_p=63 ;
    static int TAG_SAMP=88,       TAG_samp=89 ;
    static int TAG_SCRIPT=146,    TAG_script=147 ;
    static int TAG_SELECT=180,    TAG_select=181 ;
    static int TAG_SMALL=120,     TAG_small=121 ;
    static int TAG_SPAN=86,       TAG_span=87 ;
    static int TAG_STRIKE=214,    TAG_strike=215 ;
    static int TAG_STRONG=110,    TAG_strong=111 ;
    static int TAG_STYLE=140,     TAG_style=141 ;
    static int TAG_SUB=218,       TAG_sub=219 ;
    static int TAG_SUP=220,       TAG_sup=221 ;
    static int TAG_TABLE=78,      TAG_table=79 ;
    static int TAG_TEXTAREA=184,  TAG_textarea=185 ;
    static int TAG_TITLE=134,     TAG_title=135 ;
    static int TAG_TD=84,         TAG_td=85 ;
    static int TAG_TH=80,         TAG_th=81 ;
    static int TAG_TR=82,         TAG_tr=83 ;
    static int TAG_TT=112,        TAG_tt=113 ;
    static int TAG_UL=46,         TAG_ul=47 ;
    static int TAG_U=106,         TAG_u=107 ;
    static int TAG_VAR=216,       TAG_var=217 ;
    static int TAG_BLINK=190,     TAG_blink=191 ;
    static int TAG_NOFRAME=154,   TAG_noframe=155 ;
    static int TAG_FIG=188,       TAG_fig=189 ;
    static int TAG_MARQUEE=192,   TAG_marquee=193 ;
    static int TAG_MATH=198,      TAG_math=199 ;
    static int TAG_LIST=200,      TAG_list=201 ;
    static int TAG_NOBR=74,       TAG_nobr=75 ;
    static int TAG_LEFT=194,      TAG_left=195 ;
    static int TAG_RIGHT=196,     TAG_right=197 ;
    static int TAG_XMP=144,       TAG_xmp=145 ;
    static int TAG_NOLAYER=174,   TAG_nolayer=175 ;
    static int TAG_SPACER=206,    TAG_spacer=207 ;
    static int TAG_LAYER=160,     TAG_layer=161 ;
    static int TAG_ILAYER=172,    TAG_ilayer=173 ;
    static int TAG_COMMENT=202,   TAG_comment=203 ;
    static int TAG_STRING=33,     TAG_string=34 ;

    // When extending this list, make sure that substring collisions
    // do not introduce bugs. For example: tag "A" has to come after
    // "ADDRESS"; otherwise all "ADDRESS" tag will be seen as "A" tags.

    String[] tags = {
                     "ACRONYM", "ADDRESS", "APPLET", "AREA", "A",
                     "BASEFONT", "BASE", "BDO", "BIG", "BLOCKQUOTE",
                     "BODY", "BR", "BUTTON", "B",
                     "CAPTION", "CENTER", "CITE", "CODE","COLGROUP", "COL",
                     "DD", "DEL", "DFN", "DIR", "DIV","DL", "DT",
                     "EM",
                     "FIELDSET", "FONT", "FORM", "FRAMESET", "FRAME",
                     "H1", "H2", "H3", "H4", "H5", "H6",
                     "HEAD", "HR", "HTML",
                     "IFRAME", "IMG", "INPUT", "INS", "ISINDEX", "I",
                     "KBD",
                     "LABEL", "LEGENT", "LINK", "LI",
                     "MAP", "MENU", "META",
                     "NOFRAMES", "NOSCRIPT",
                     "OBJECT", "OL", "OPTION",
                     "PARAM", "PRE", "P",
                     "Q",
                     "SAMP", "SCRIPT", "SELECT", "SMALL", "SPAN", "STRIKE",
                     "STRONG", "STYLE", "SUB", "SUP", "S",
                     "TABLE", "TBODY", "TEXTAREA", "TFOOT", "THEAD", "TITLE",
                     "TD", "TH", "TR", "TT",
                     "UL", "U",
                     "VAR","BLINK","NOFRAME","FIG","MARQUEE","MATH",
                     "LIST","NOBR","LEFT","RIGHT","XMP","LISTING",
                     "NOLAYER","SPACER","LAYER","ILAYER","COMMENT",
                     "!DOCTYPE", "!--" ,"STRING"
                     };

                        //for alignment//
     static int tagcode[][]={{ -6 , -4 , -4 , -4 },// ACRONYM  --
                                                      { -6 ,0x44,0x44,0x44},// ADDRESS  68
                                                      { -6 ,0x94,0x94,0x94},// APPLET  148
                                                      { -6 , -4 ,0xd2, -4 },// AREA  210
                                   /*****/    {0x62,0x62,0x62, -4 },// A  98
                                                      { -6 , -4 , -4 ,0xd4},// BASEFONT  212
                                                      { -6 ,0x88,0x88,0x88},// BASE  136
                                                      { -6 , -4 , -4 , -4 },// BDO  --
                                                      { -6 , -4 , -4 ,0x76},// BIG  118
                                                      { -6 ,0x42,0x42,0x42},// BLOCKQUOTE  66
                                                      { -6 ,0x84,0x84,0x84},// BODY  132
                                   /*****/    { -4 ,-4,0x70,0x70},// BR  112
                                                      { -6 , -4 , -4 , -4 },// BUTTON  --
                                   /*****/    { -4 , -4 , -4 ,0x66},// B  102
                                                      { -6 ,0xcc,0xcc,0xcc},// CAPTION  204
                                                      { -6 ,0x48,0x48,0x48},// CENTER  72
                                                      { -6 , -4 ,0x4c,0x4c},// CITE  76
                                                      { -6 , -4 , -4 ,0x72},// CODE  114
                                                      { -6 , -4 , -4 , -4 },// COLGROUP  --
                                                      { -6 , -4 , -4 , -4 },// COL  --
                                                      { -6 ,0x3c,0x3c,0x3c},// DD  60
                                                      { -6 , -4 , -4 , -4 },// DEL  --
                                                      { -6 , -4 ,0x7a,0x7a},// DFN  122
                                                      { -6 ,0x32,0x32,0x32},// DIR  50
                                                      { -6 ,0x46,0x46,0x46},// DIV  70
                                                      { -6 ,0x38,0x38,0x38},// DL  56
                                                      { -6 ,0x3a,0x3a,0x3a},// DT  58
                                                      { -6 , -4 , -4 ,0x6c},// EM  108
                                                      { -6 , -4 , -4 , -4 },// FIELDSET  --
                                   /*****/    { -4 , -4 , -4 ,0x64},// FONT  100
                                                      { -6 ,0xb0,0xb0,0xb0},// FORM  176
                                                      { -6 ,0x9c,0x9c,0x9c},// FRAMESET  156
                                                      { -6 ,0x98,0x98,0x98},// FRAME  152
                                                      { -6 ,0x22,0x22,0x22},// H1  34
                                                      { -6 ,0x26,0x26,0x26},// H2  38
                                                      { -6 ,0x24,0x24,0x24},// H3  36
                                                      { -6 ,0x28,0x28,0x28},// H4  40
                                                      { -6 ,0x2a,0x2a,0x2a},// H5  42
                                                      { -6 ,0x5e,0x5e,0x5e},// H6  94
                                                      { -6 ,0x82,0x82,0x82},// HEAD  130
                                                      { -6 ,0x76,0x76,0x76},// HR  118
                                                      { -6 ,0x80,0x80,0x80},// HTML  128
                                                      { -6 ,0x9e,0x9e,0x9e},// IFRAME  158
                                   /*****/    { -4,  -4 ,0x60, -4 },// IMG  96
                                                      { -6 ,0xb2,0xb2,0xb2},// INPUT  178
                                                      { -6 , -4 , -4 , -4 },// INS  --
                                                      { -4 ,0xba,0xba,0xba},// ISINDEX  186
                                                      { -6 , -4 , -4 ,0x68},// I  104
                                                      { -6 , -4 , -4 ,0x74},// KBD  116
                                                      { -6 , -4 , -4 , -4 },// LABEL  --
                                                      { -6 , -4 , -4 , -4 },// LEGENT  --
                                                      { -6 ,0x8e,0x8e,0x8e},// LINK  142
                                                      { -6 ,0x36,0x36,0x36},// LI  54
                                                      { -6 , -4 ,0xd0, -4 },// MAP  208
                                                      { -6 ,0x34,0x34,0x34},// MENU  52
                                                      { -6 ,0x8a,0x8a,0x8a},// META  138
                                                      { -6 , -4 , -4 , -4 },// NOFRAMES  --
                                                      { -6 , -4 , -4 , -4 },// NOSCRIPT  --
                                                      { -6 , -4 , -4 , -4 },// OBJECT  --
                                                      { -6 ,0x30,0x30,0x30},// OL  48
                                                      { -6 ,0xb6,0xb6,0xb6},// OPTION  182
                                                      { -6 ,0x96,0x96,0x96},// PARAM  150
                                                      { -6 ,0x40,0x40,0x40},// PRE  64
                                                      { -4 ,0x3e,0x3e,0x3e},// P  62
                                                      { -6 , -4 , -4 , -4 },// Q  --
                                                      { -6 , -4 ,0x58,0x58},// SAMP  88
                                                      { -6 ,0x92,0x92,0x92},// SCRIPT  146
                                                      { -6 ,0xb4,0xb4,0xb4},// SELECT  180
                                                      { -6 , -4 , -4 ,0x78},// SMALL  120
                                     /*05-09-19*/     { -4 , -4 ,0x56,0x56},// SPAN  86
                                                      { -6 , -4 , -4 ,0xd6},// STRIKE  214
                                                      { -6 , -4 , -4 ,0x6e},// STRONG  110
                                                      { -6 ,0x8c,0x8c,0x8c},// STYLE  140
                                                      { -6 , -4 ,0xda,0xda},// SUB  218
                                                      { -6 , -4 ,0xdc,0xdc},// SUP  220
                                                      { -6 , -4 , -4 , -4 },// S  --
                                                      { -6 ,0x4e,0x4e,0x4e},// TABLE  78
                                                      { -6 , -4 , -4 , -4 },// TBODY  --
                                                      { -6 ,0xb8,0xb8,0xb8},// TEXTAREA  184
                                                      { -6 , -4 , -4 , -4 },// TFOOT  --
                                                      { -6 , -4 , -4 , -4 },// THEAD  --
                                                      { -6 ,0x86,0x86,0x86},// TITLE  134
                                                      { -6 ,0x54,0x54,0x54},// TD  84
                                                      { -6 ,0x50,0x50,0x50},// TH  80
                                                      { -6 ,0x52,0x52,0x52},// TR  82
                                                      { -6 , -4 , -4 ,0x70},// TT  112
                                                      { -6 ,0x2e,0x2e,0x2e},// UL  46
                                                      { -6 , -4 , -4 ,0x6a},// U  106
                                                      { -6 , -4 ,0xd8,0xd8},// VAR  216
                                                      { -6 ,0xbe,0xbe,0xbe},// BLINK  190
                                                      { -6 ,0x9a,0x9a,0x9a},// NOFRAME  154
                                                      { -6 ,0xbc,0xbc,0xbc},// FIG  188
                                                      { -6 ,0xc0,0xc0,0xc0},// MARQUEE  192
                                                      { -6 ,0xc6,0xc6,0xc6},// MATH  198
                                                      { -6 ,0xc8,0xc8,0xc8},// LIST  200
                                                      { -6 ,0x4a,0x4a,0x4a},// NOBR  74
                                                      { -6 ,0xc2,0xc2,0xc2},// LEFT  194
                                                      { -6 ,0xc4,0xc4,0xc4},// RIGHT  196
                                                      { -6 ,0x90,0x90,0x90},// XMP  144
                                                      { -6 , -4 , -4 , -4 },// LISTING  --
                                                      { -6 ,0xae,0xae,0xae},// NOLAYER  174
                                                      { -6 ,0xce,0xce,0xce},// SPACER  206
                                                      { -6 ,0xa0,0xa0,0xa0},// LAYER  160
                                                      { -6 ,0xac,0xac,0xac},// ILAYER  172
                                                      { -6 ,0xca,0xca,0xca},// COMMENT  202
                                                      { -6 , -4 , -4 , -4 },// !DOCTYPE  --
                                                      { -6 , -4 , -4 , -4 },// !--  --
                                                      {0x21,0x21,0x21,0x21},// STRING  33
                                      };
 } // End of class HTMLTokenizer
