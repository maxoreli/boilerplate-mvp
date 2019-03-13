package com.tizisolutions.smartcacherxjava2calladapter;

import android.util.Log;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Iterator;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import retrofit2.Converter;
import retrofit2.Retrofit;

public final class SmartUtils {
    public SmartUtils() {
    }

    public static <T> byte[] responseToBytes(Retrofit retrofit, T data, Type dataType, Annotation[] annotations) {
        Iterator var4 = retrofit.converterFactories().iterator();

        while(true) {
            Converter converter;
            do {
                Converter.Factory factory;
                do {
                    if (!var4.hasNext()) {
                        return null;
                    }

                    factory = (Converter.Factory)var4.next();
                } while(factory == null);

                converter = factory.requestBodyConverter(dataType, annotations, (Annotation[])null, retrofit);
            } while(converter == null);

            Buffer buff = new Buffer();

            try {
                ((RequestBody)converter.convert(data)).writeTo(buff);
                return buff.readByteArray();
            } catch (IOException var9) {
                ;
            }
        }
    }

    public static <T> T bytesToResponse(Retrofit retrofit, Type dataType, Annotation[] annotations, byte[] data) {
        Iterator var4 = retrofit.converterFactories().iterator();

        while(true) {
            Converter converter;
            do {
                Converter.Factory factory;
                do {
                    if (!var4.hasNext()) {
                        return null;
                    }

                    factory = (Converter.Factory)var4.next();
                } while(factory == null);

                converter = factory.responseBodyConverter(dataType, annotations, retrofit);
            } while(converter == null);

            try {
                return (T) converter.convert(ResponseBody.create((MediaType)null, data));
            } catch (NullPointerException | IOException var8) {
                Log.e("SmartCall", "", var8);
            }
        }
    }
}