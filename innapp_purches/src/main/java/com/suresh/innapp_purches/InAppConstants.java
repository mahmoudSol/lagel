package com.suresh.innapp_purches;

import java.util.ArrayList;
import java.util.Collections;
/**
 * @since  15/4/16.
 */
public class InAppConstants
{
    public static final String base64EncodedPublicKey ="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAswBzyReXU5pnRoydIxgAj/bWBshU43AaQiZPc+MDF+qUJg+IabRRIQpYQJ3F5Xbmk7+jUXy8BoBILo8Alyz4rM/mrzCr+2VJ27bohn2sMAElA6LWhc+TDBHtIn/mMf15ydg6BdFlixDv86u4mluddQQRcfngD7nKZcog5dkHN+qCazYuutAyIFmmeDSzmns/1RxTFLBXLIZScEd3m6VVAADGzPCcwQJ2nISLJ5NtkZ3lTHxMEUmfCGQWyLiMXpdxqFEjPrUbECmLuwZjmbeYdgJJ60fdNhgGVg1pQ5LyMALHH9+gbRnBTA2KD3dJJVEUapCcvB7BxClJt84pc4dKiwIDAQAB".trim();
    /*
     *purchase item details. */
    public enum Purchase_item
    {
        LOWER_PRICE("com.lagel.100Clicks","1503151128429"),
        MEDIUM_PRICE("com.lagel.200Clicks","1503153105155"),
        HIGH_PRICE("com.lagel.300Clicks","1504019528923"),
        HIGEST_PRICE("com.lagel.500Clicks","1499918013413"),
        TEST_CASES("com.test.purchased","");

        public static Purchase_item getPurchaseItem(String id)
        {
            Purchase_item purchase_item=null;
            ArrayList<Purchase_item> purchase_items = new ArrayList<>();
            Collections.addAll(purchase_items,Purchase_item.values());
            for(Purchase_item item:purchase_items)
            {
                if(item.getId().equals(id))
                {
                    purchase_item=item;
                    break;
                }
            }
            if(purchase_item==null)
            {
                purchase_item=TEST_CASES;
            }
            return purchase_item;
        }
        /*
         *Getting the plan Id */
        public static Purchase_item getPlanId(String playKey)
        {
            Purchase_item purchase_item=null;
            ArrayList<Purchase_item> purchase_items = new ArrayList<>();
            Collections.addAll(purchase_items,Purchase_item.values());
            for(Purchase_item item:purchase_items)
            {
                if(item.getKey().equals(playKey))
                {
                    purchase_item=item;
                    break;
                }
            }
            return purchase_item;
        }

        private String value;
        private String id;
        public String getId()
        {
            return this.id;
        }
        public String getKey()
        {
            return this.value;
        }
        Purchase_item(String value,String id)
        {
            this.value = value;
            this.id=id;
        }
    }

}
