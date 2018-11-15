#pragma version(1)
#pragma rs java_package_name(com.tifone.demo.camera.rs)

#include "rs_debug.rsh"

static const float4 weight = {0.299f, 0.587f, 0.114f, 0.0f};

uchar4 RS_KERNEL invert(uchar4 in, uint32_t x, uint32_t y) {
    uchar4 out = in;
    out.x = 255 - in.x / 2;
    out.y = 255 - in.y / 2;
    out.z = 255 - in.z / 2;
    rsDebug("tifone", out.x);
    return out;
}
uchar4 __attribute__((kernel)) sketch(uchar4 in, uint32_t x, uint32_t y) {
    uchar4 out = in;

    float r = out.r;
    float g = out.g;
    float b = out.b;
    float avg = (r + g + b) / 3;
    if (avg >= 200) {
        return rsPackColorTo8888(1,1,1, out.a);
    } else if (avg > 100) {
        return rsPackColorTo8888(1,1,1, 0.5);
    } else if (avg > 50) {
        return rsPackColorTo8888(0,0,0, 0.5);
    } else {
       return rsPackColorTo8888(0,0,0, out.a);
    }

}

int atX;
int atY;
float radius;
float scale = 1.5f;
rs_allocation inputAllocation;
int maxWidth;
int maxHeight;
uchar4 __attribute__((kernel)) magnify(uchar4 in, int x, int y) {

    float pointDistanceFromCircleCenter =
            sqrt(pow((float)(x - atX), 2) + pow((float)(y - atY), 2));
    if (radius < pointDistanceFromCircleCenter) {
        return in;
    }

    int diffX = x - atX;
    int diffY = y - atY;

    int originalX = atX + round(diffX / scale);
    int originalY = atY + round(diffY / scale);
    if (originalX < 0 || originalY < 0 || originalX > maxWidth || originalY > maxHeight) {
        return rsPackColorTo8888(0,0,0,in.a);
    }
    uchar4 result = rsGetElementAt_uchar4(inputAllocation, originalX, originalY);
    return result;
}

uchar4 RS_KERNEL greyscale(uchar4 in) {
    const float4 inF = rsUnpackColor8888(in);
    const float4 outF = (float4){ dot(inF, weight)};
    return rsPackColorTo8888(outF);
}

void process(rs_allocation inputImage, rs_allocation outputImage) {
    inputAllocation = inputImage;
    const uint32_t imageWidth = rsAllocationGetDimX(inputImage);
    const uint32_t imageHeight = rsAllocationGetDimY(inputImage);
    //atX = 500;//imageWidth / 2 ;
    //atY = 300;//imageHeight / 2;
    maxWidth = imageWidth;
    maxHeight = imageHeight;
    int minSize = min(imageWidth / 2, imageHeight / 2);
    radius = minSize;
    rsDebug("atX = ", atX);
    rsDebug("atY = ", atY);
    rsDebug("radius = ", radius);
    rs_allocation tmp = rsCreateAllocation_uchar4(imageWidth, imageHeight);
    //rsForEach(invert, inputImage, outputImage);
    //rsForEach(greyscale, inputImage, outputImage);
    //rsForEach(sketch, inputImage, outputImage);
    rsForEach(magnify, inputImage, outputImage);
}
