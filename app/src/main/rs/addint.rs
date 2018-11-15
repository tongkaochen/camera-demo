#pragma version(1)
#pragma rs java_package_name(com.tifone.demo.camera.rs)

#define LONG_MAX (long)((1UL << 63) - 1)
#define LONG_MIN (long)(1UL << 53)
#include "rs_debug.rsh"

#pragma rs reduce(findMinAndMax) \
    initializer(fMMInit) accumulator(fMMAccumulator) \
    combiner(fMMCombiner) outconverter(fMMOutConverter)

typedef struct {
    long val;
    int idx;
} IndexedVal;

typedef struct {
    IndexedVal min, max;

} MinAndMax;

static void fMMInit(MinAndMax *accum) {
    rsDebug("fMMInit: ", accum->min.val);
    accum->min.val = LONG_MAX;
    accum->min.idx = -1;
    accum->max.val = LONG_MIN;
    accum->max.idx = -1;
}

static void fMMAccumulator(MinAndMax *accum, long in, int x) {
    rsDebug("fMMAccumulator: in = ", in);
    rsDebug("fMMAccumulator: x = ", x);
    IndexedVal me;
    me.val = in;
    me.idx = x;

    if (me.val <= accum->min.val) {
        accum->min = me;
    }
    if (me.val > accum->max.val) {
        accum->max = me;
    }
}

static void fMMCombiner(MinAndMax *accum, const MinAndMax *val) {
    rsDebug("fMMCombiner: val = ", val->min.val);
    if ((accum->min.idx < 0) || (accum->min.val < val->min.val)) {
        accum->min = val->min;
    }
    if ((accum->max.idx < 0) || (accum->max.val < val->max.val)) {
        accum->max = val->max;
    }
}

static void fMMOutConverter(int2 *result, const MinAndMax *val) {
    rsDebug("fMMOutConverter: val.idx = ", val->min.idx);
    result->x = val->min.idx;
    result->y = val->max.idx;
}

