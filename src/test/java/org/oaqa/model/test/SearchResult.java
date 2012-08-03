

/* First created by JCasGen Fri Aug 03 13:49:08 EDT 2012 */
package org.oaqa.model.test;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.IntegerArray;
import org.apache.uima.jcas.cas.DoubleArray;


/** A search result.
 * Updated by JCasGen Fri Aug 03 13:49:08 EDT 2012
 * XML source: /Users/elmer/Documents/workspace/oaqa/edu.cmu.lti.oaqa.cse.CseFramework/src/test/resources/edu/cmu/lti/oaqa/TestTypes.xml
 * @generated */
public class SearchResult extends OAQATop {
  /** @generated
   * @ordered 
   */
  public final static int typeIndexID = JCasRegistry.register(SearchResult.class);
  /** @generated
   * @ordered 
   */
  public final static int type = typeIndexID;
  /** @generated  */
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected SearchResult() {}
    
  /** Internal - constructor used by generator 
   * @generated */
  public SearchResult(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public SearchResult(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** <!-- begin-user-doc -->
    * Write your own initialization here
    * <!-- end-user-doc -->
  @generated modifiable */
  private void readObject() {}
     
 
    
  //*--------------*
  //* Feature: uri

  /** getter for uri - gets An identifier for the source of this search result, e.g., the name of the file that this result is drawn from, which, when combined with offset and length, uniquely identifies the span of text for this result.
   * @generated */
  public String getUri() {
    if (SearchResult_Type.featOkTst && ((SearchResult_Type)jcasType).casFeat_uri == null)
      jcasType.jcas.throwFeatMissing("uri", "org.oaqa.model.test.SearchResult");
    return jcasType.ll_cas.ll_getStringValue(addr, ((SearchResult_Type)jcasType).casFeatCode_uri);}
    
  /** setter for uri - sets An identifier for the source of this search result, e.g., the name of the file that this result is drawn from, which, when combined with offset and length, uniquely identifies the span of text for this result. 
   * @generated */
  public void setUri(String v) {
    if (SearchResult_Type.featOkTst && ((SearchResult_Type)jcasType).casFeat_uri == null)
      jcasType.jcas.throwFeatMissing("uri", "org.oaqa.model.test.SearchResult");
    jcasType.ll_cas.ll_setStringValue(addr, ((SearchResult_Type)jcasType).casFeatCode_uri, v);}    
   
    
  //*--------------*
  //* Feature: score

  /** getter for score - gets Relevance score associated with the search result.
   * @generated */
  public double getScore() {
    if (SearchResult_Type.featOkTst && ((SearchResult_Type)jcasType).casFeat_score == null)
      jcasType.jcas.throwFeatMissing("score", "org.oaqa.model.test.SearchResult");
    return jcasType.ll_cas.ll_getDoubleValue(addr, ((SearchResult_Type)jcasType).casFeatCode_score);}
    
  /** setter for score - sets Relevance score associated with the search result. 
   * @generated */
  public void setScore(double v) {
    if (SearchResult_Type.featOkTst && ((SearchResult_Type)jcasType).casFeat_score == null)
      jcasType.jcas.throwFeatMissing("score", "org.oaqa.model.test.SearchResult");
    jcasType.ll_cas.ll_setDoubleValue(addr, ((SearchResult_Type)jcasType).casFeatCode_score, v);}    
   
    
  //*--------------*
  //* Feature: text

  /** getter for text - gets The text associated with the hit.
   * @generated */
  public String getText() {
    if (SearchResult_Type.featOkTst && ((SearchResult_Type)jcasType).casFeat_text == null)
      jcasType.jcas.throwFeatMissing("text", "org.oaqa.model.test.SearchResult");
    return jcasType.ll_cas.ll_getStringValue(addr, ((SearchResult_Type)jcasType).casFeatCode_text);}
    
  /** setter for text - sets The text associated with the hit. 
   * @generated */
  public void setText(String v) {
    if (SearchResult_Type.featOkTst && ((SearchResult_Type)jcasType).casFeat_text == null)
      jcasType.jcas.throwFeatMissing("text", "org.oaqa.model.test.SearchResult");
    jcasType.ll_cas.ll_setStringValue(addr, ((SearchResult_Type)jcasType).casFeatCode_text, v);}    
   
    
  //*--------------*
  //* Feature: rank

  /** getter for rank - gets Rank of this result in the original hit-list.
   * @generated */
  public int getRank() {
    if (SearchResult_Type.featOkTst && ((SearchResult_Type)jcasType).casFeat_rank == null)
      jcasType.jcas.throwFeatMissing("rank", "org.oaqa.model.test.SearchResult");
    return jcasType.ll_cas.ll_getIntValue(addr, ((SearchResult_Type)jcasType).casFeatCode_rank);}
    
  /** setter for rank - sets Rank of this result in the original hit-list. 
   * @generated */
  public void setRank(int v) {
    if (SearchResult_Type.featOkTst && ((SearchResult_Type)jcasType).casFeat_rank == null)
      jcasType.jcas.throwFeatMissing("rank", "org.oaqa.model.test.SearchResult");
    jcasType.ll_cas.ll_setIntValue(addr, ((SearchResult_Type)jcasType).casFeatCode_rank, v);}    
   
    
  //*--------------*
  //* Feature: queryString

  /** getter for queryString - gets The query string associated with the hit.
   * @generated */
  public String getQueryString() {
    if (SearchResult_Type.featOkTst && ((SearchResult_Type)jcasType).casFeat_queryString == null)
      jcasType.jcas.throwFeatMissing("queryString", "org.oaqa.model.test.SearchResult");
    return jcasType.ll_cas.ll_getStringValue(addr, ((SearchResult_Type)jcasType).casFeatCode_queryString);}
    
  /** setter for queryString - sets The query string associated with the hit. 
   * @generated */
  public void setQueryString(String v) {
    if (SearchResult_Type.featOkTst && ((SearchResult_Type)jcasType).casFeat_queryString == null)
      jcasType.jcas.throwFeatMissing("queryString", "org.oaqa.model.test.SearchResult");
    jcasType.ll_cas.ll_setStringValue(addr, ((SearchResult_Type)jcasType).casFeatCode_queryString, v);}    
   
    
  //*--------------*
  //* Feature: searchId

  /** getter for searchId - gets ID of the search object to which this search result belongs (matches the searchId field in the Search object).
   * @generated */
  public String getSearchId() {
    if (SearchResult_Type.featOkTst && ((SearchResult_Type)jcasType).casFeat_searchId == null)
      jcasType.jcas.throwFeatMissing("searchId", "org.oaqa.model.test.SearchResult");
    return jcasType.ll_cas.ll_getStringValue(addr, ((SearchResult_Type)jcasType).casFeatCode_searchId);}
    
  /** setter for searchId - sets ID of the search object to which this search result belongs (matches the searchId field in the Search object). 
   * @generated */
  public void setSearchId(String v) {
    if (SearchResult_Type.featOkTst && ((SearchResult_Type)jcasType).casFeat_searchId == null)
      jcasType.jcas.throwFeatMissing("searchId", "org.oaqa.model.test.SearchResult");
    jcasType.ll_cas.ll_setStringValue(addr, ((SearchResult_Type)jcasType).casFeatCode_searchId, v);}    
   
    
  //*--------------*
  //* Feature: featureVector

  /** getter for featureVector - gets Passage Level scores
   * @generated */
  public DoubleArray getFeatureVector() {
    if (SearchResult_Type.featOkTst && ((SearchResult_Type)jcasType).casFeat_featureVector == null)
      jcasType.jcas.throwFeatMissing("featureVector", "org.oaqa.model.test.SearchResult");
    return (DoubleArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((SearchResult_Type)jcasType).casFeatCode_featureVector)));}
    
  /** setter for featureVector - sets Passage Level scores 
   * @generated */
  public void setFeatureVector(DoubleArray v) {
    if (SearchResult_Type.featOkTst && ((SearchResult_Type)jcasType).casFeat_featureVector == null)
      jcasType.jcas.throwFeatMissing("featureVector", "org.oaqa.model.test.SearchResult");
    jcasType.ll_cas.ll_setRefValue(addr, ((SearchResult_Type)jcasType).casFeatCode_featureVector, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for featureVector - gets an indexed value - Passage Level scores
   * @generated */
  public double getFeatureVector(int i) {
    if (SearchResult_Type.featOkTst && ((SearchResult_Type)jcasType).casFeat_featureVector == null)
      jcasType.jcas.throwFeatMissing("featureVector", "org.oaqa.model.test.SearchResult");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((SearchResult_Type)jcasType).casFeatCode_featureVector), i);
    return jcasType.ll_cas.ll_getDoubleArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((SearchResult_Type)jcasType).casFeatCode_featureVector), i);}

  /** indexed setter for featureVector - sets an indexed value - Passage Level scores
   * @generated */
  public void setFeatureVector(int i, double v) { 
    if (SearchResult_Type.featOkTst && ((SearchResult_Type)jcasType).casFeat_featureVector == null)
      jcasType.jcas.throwFeatMissing("featureVector", "org.oaqa.model.test.SearchResult");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((SearchResult_Type)jcasType).casFeatCode_featureVector), i);
    jcasType.ll_cas.ll_setDoubleArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((SearchResult_Type)jcasType).casFeatCode_featureVector), i, v);}
   
    
  //*--------------*
  //* Feature: rankVector

  /** getter for rankVector - gets Passage level ranks
   * @generated */
  public IntegerArray getRankVector() {
    if (SearchResult_Type.featOkTst && ((SearchResult_Type)jcasType).casFeat_rankVector == null)
      jcasType.jcas.throwFeatMissing("rankVector", "org.oaqa.model.test.SearchResult");
    return (IntegerArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((SearchResult_Type)jcasType).casFeatCode_rankVector)));}
    
  /** setter for rankVector - sets Passage level ranks 
   * @generated */
  public void setRankVector(IntegerArray v) {
    if (SearchResult_Type.featOkTst && ((SearchResult_Type)jcasType).casFeat_rankVector == null)
      jcasType.jcas.throwFeatMissing("rankVector", "org.oaqa.model.test.SearchResult");
    jcasType.ll_cas.ll_setRefValue(addr, ((SearchResult_Type)jcasType).casFeatCode_rankVector, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for rankVector - gets an indexed value - Passage level ranks
   * @generated */
  public int getRankVector(int i) {
    if (SearchResult_Type.featOkTst && ((SearchResult_Type)jcasType).casFeat_rankVector == null)
      jcasType.jcas.throwFeatMissing("rankVector", "org.oaqa.model.test.SearchResult");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((SearchResult_Type)jcasType).casFeatCode_rankVector), i);
    return jcasType.ll_cas.ll_getIntArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((SearchResult_Type)jcasType).casFeatCode_rankVector), i);}

  /** indexed setter for rankVector - sets an indexed value - Passage level ranks
   * @generated */
  public void setRankVector(int i, int v) { 
    if (SearchResult_Type.featOkTst && ((SearchResult_Type)jcasType).casFeat_rankVector == null)
      jcasType.jcas.throwFeatMissing("rankVector", "org.oaqa.model.test.SearchResult");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((SearchResult_Type)jcasType).casFeatCode_rankVector), i);
    jcasType.ll_cas.ll_setIntArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((SearchResult_Type)jcasType).casFeatCode_rankVector), i, v);}
  }

    