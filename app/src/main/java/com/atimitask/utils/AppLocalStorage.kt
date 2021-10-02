package com.atimitask.utils

import android.content.Context
import android.content.SharedPreferences
import com.atimitask.BuildConfig

class AppLocalStorage {

    private var appStorage: AppLocalStorage? = null
    protected var mContext: Context? = null
    private var mSharedPreferences: SharedPreferences? = null
    private var mSharedPreferencesEditor: SharedPreferences.Editor? = null

    companion object {
        val SP_FAVOURITES = "SP_FAVOURITES"
    }

    private fun AppLocalStorage(context: Context): AppLocalStorage {
        mContext = context
        mSharedPreferences =
            context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
        mSharedPreferencesEditor = mSharedPreferences?.edit()
        return this
    }

    /**
     * Creates single instance of SharedPreferenceUtils
     *
     * @param context context of Activity or Service
     * @return Returns instance of SharedPreferenceUtils
     */
    @Synchronized
    fun getInstance(context: Context): AppLocalStorage? {
        if (appStorage == null) {
            appStorage = AppLocalStorage(context)
        }
        return appStorage
    }

    /**
     * Stores String value in preference
     *
     * @param key   key of preference
     * @param value value for that key
     */
    fun setValue(key: String?, value: String?) {
        mSharedPreferencesEditor!!.putString(key, value)
        mSharedPreferencesEditor!!.commit()
    }

    /**
     * Stores int value in preference
     *
     * @param key   key of preference
     * @param value value for that key
     */
    fun setValue(key: String?, value: Int) {
        mSharedPreferencesEditor!!.putInt(key, value)
        mSharedPreferencesEditor!!.commit()
    }

    /**
     * Stores Double value in String format in preference
     *
     * @param key   key of preference
     * @param value value for that key
     */
    fun setValue(key: String?, value: Double) {
        setValue(key, java.lang.Double.toString(value))
    }

    /**
     * Stores long value in preference
     *
     * @param key   key of preference
     * @param value value for that key
     */
    fun setValue(key: String?, value: Long) {
        mSharedPreferencesEditor!!.putLong(key, value)
        mSharedPreferencesEditor!!.commit()
    }

    /**
     * Stores boolean value in preference
     *
     * @param key   key of preference
     * @param value value for that key
     */
    fun setValue(key: String?, value: Boolean) {
        mSharedPreferencesEditor!!.putBoolean(key, value)
        mSharedPreferencesEditor!!.commit()
    }

    /**
     * Retrieves String value from preference
     *
     * @param key          key of preference
     * @param defaultValue default value if no key found
     */
    fun getValue(key: String?, defaultValue: String?): String? {
        return mSharedPreferences!!.getString(key, defaultValue)
    }

    /**
     * Retrieves int value from preference
     *
     * @param key          key of preference
     * @param defaultValue default value if no key found
     */
    fun getValue(key: String?, defaultValue: Int): Int {
        return mSharedPreferences!!.getInt(key, defaultValue)
    }

    /**
     * Retrieves long value from preference
     *
     * @param key          key of preference
     * @param defaultValue default value if no key found
     */
    fun getValue(key: String?, defaultValue: Long): Long {
        return mSharedPreferences!!.getLong(key, defaultValue)
    }

    /**
     * Retrieves boolean value from preference
     *
     * @param keyFlag      key of preference
     * @param defaultValue default value if no key found
     */
    fun getValue(keyFlag: String?, defaultValue: Boolean): Boolean {
        return mSharedPreferences!!.getBoolean(keyFlag, defaultValue)
    }

    /**
     * Removes key from preference
     *
     * @param key key of preference that is to be deleted
     */
    fun removeKey(key: String?) {
        if (mSharedPreferencesEditor != null) {
            mSharedPreferencesEditor!!.remove(key)
            mSharedPreferencesEditor!!.commit()
        }
    }

    /**
     * Clears all the preferences stored
     */
    fun clear() {
        mSharedPreferencesEditor!!.clear().commit()
    }

}