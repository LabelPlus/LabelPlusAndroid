//
// Created by aya on 2016/05/30.
//

#include <cstdio>
#include <cstdlib>
#include "common.h"
#include "io_github_sgqy_labeler_Extra.h"
#include "Scanner.hpp"

jboolean JNICALL Java_io_github_sgqy_labeler_Extra_ScanFile
  (JNIEnv* env, jobject obj, jstring infile, jstring outfile)
{
    const char* i = env->GetStringUTFChars(infile, 0);
    const char* o = env->GetStringUTFChars(outfile, 0);


    // test read permission
    FILE* fp = fopen(i, "rb");
    if(fp == 0)
    { return false; }
    else
    { fclose(fp); }

    // test write permission
    fp = fopen(o, "wb+");
    if(fp == 0)
    { return false; }
    else
    {
        fclose(fp);
        std::remove(o);
    }

    try
    {
        if(scan(i, o) == true)
        { return JNI_TRUE; }
        else
        { return JNI_FALSE; }
    }
    catch(...)
    {
        return JNI_FALSE;
    }
}