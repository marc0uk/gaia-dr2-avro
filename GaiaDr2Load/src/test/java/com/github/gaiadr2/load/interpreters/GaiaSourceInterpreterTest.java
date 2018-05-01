package com.github.gaiadr2.load.interpreters;

import com.github.gaiadr2.avro.source.AstroParams;
import com.github.gaiadr2.avro.source.AstrometryMetadata;
import com.github.gaiadr2.avro.source.BandPhot;
import com.github.gaiadr2.avro.source.ColourInfo;
import com.github.gaiadr2.avro.source.CoreAstro;
import com.github.gaiadr2.avro.source.GaiaSource;
import com.github.gaiadr2.avro.source.OptAstro;
import com.github.gaiadr2.avro.source.PhotMode;
import com.github.gaiadr2.avro.source.RotatorObjectType;
import com.github.gaiadr2.avro.source.Rvs;
import com.github.gaiadr2.avro.source.VarFlag;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static com.github.gaiadr2.load.interpreters.TestUtil.assertEquals;
import static com.github.gaiadr2.load.interpreters.TestUtil.assertFloat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit test for {@link GaiaSourceInterpreter}
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
final class GaiaSourceInterpreterTest {

    enum Item {
        AllExceptRvs1,
        AllExceptRvs2,
        FullPhotNoRvsNoParam1,
        FullPhotNoRvsNoParam2,
        Everything,
        Minimal
    }

    private final List<String> lines = new ArrayList<>();
    private final Function<String, GaiaSource> interpreter = new GaiaSourceInterpreter();

    @BeforeAll
    void loadCsvFile() {
        lines.addAll(TestUtil.streamResource("/GaiaSource_valid.csv"));
        assertFalse(lines.isEmpty());
    }

    @Test
    void completeButRvsIsCorrectlyParsed() {
        final GaiaSource result = interpreter.apply(lines.get(Item.AllExceptRvs1.ordinal()));

        // Basic validation
        validateMandatory(result);
        assertNotNull(result.getFullAstrometry());
        assertNotNull(result.getBpPhot());
        assertNotNull(result.getRpPhot());
        assertNotNull(result.getColourInfo());
        assertNotNull(result.getAstrophysicalParameters());
        assertNull(result.getRadialVelocity());

        assertEquals(1635721458409799680L, result.getSolutionId().longValue());
        assertEquals(1000225938242805248L, result.getSourceId().longValue());
        assertEquals(1197051105L, result.getRandomIndex().longValue());
        assertEquals(22, result.getMatchedObservations().intValue());
        assertFalse(result.getDuplicated());
        assertSame(PhotMode.Gold, result.getPhotMode());
        assertSame(VarFlag.NotAvailable, result.getVarFlag());

        validateBasicAstrometry(
                result.getBasicAstrometry(),
                103.4475289523685,0.04109941963375859,
                56.02202543042615,0.04517452434341332,
                0.13175297f,
                160.16347510497707,22.53393179250832,
                98.91488373211871,32.998152413818694);

        validateFullAstrometry(
                result.getFullAstrometry(),
                0.582790372285251,0.07032848478107116,
                6.040460982932505,0.08393597680977205,
                5.055291338490129,0.07313802174143623,
                -0.12289995f,0.040914085f,-0.1964128f,
                -0.44934282f,-0.34763324f,0.32765764f,
                0.55818826f,0.08969126f,
                0.23928146f);

        validateAstroMeta(
                result.getAstroMeta(),
                184,0,
                181,3,
                0.40449554f,183.0135f,
                0.07469026106267268,0.3830316170113439,
                false,10.234882f,
                1.59291173972451,0.011422151597687636,
                -0.099905536f,
                21,10,
                0.08920107f,
                RotatorObjectType.NotUsed);

        validateBandPhot(
                result.getGPhot(),
                189,
                9268.427338702939,4.9845487064559775,
                15.77085f);

        validateBandPhot(
                result.getBpPhot(),
                21,
                4965.92379092404,15.251972317464963,
                16.111387f);

        validateBandPhot(
                result.getRpPhot(),
                21,
                6233.600689639327,12.573140640441405,
                15.275072f);

        validateColourInfo(
                result.getColourInfo(),
                1.2083522f,
                0.83631516f,
                0.34053707f,
                0.49577808f);

        validateAstroParams(
                result.getAstrophysicalParameters(),
                100001, 200111,
                5807.0f,5767.5f,5828.0f,
                0.1205f,0.017f,0.2821f,
                0.0595f,0.008f,0.1351f,
                1.0247303f,1.0173589f,1.0388145f,
                1.0757744f,0.8017981f,1.3497508f);
    }

    @Test
    void completeAstroAndPhotometryIsCorrectlyParsed() {
        final GaiaSource result = interpreter.apply(lines.get(Item.FullPhotNoRvsNoParam1.ordinal()));

        // Basic validation
        validateMandatory(result);
        assertNotNull(result.getFullAstrometry());
        assertNotNull(result.getBpPhot());
        assertNotNull(result.getRpPhot());
        assertNotNull(result.getColourInfo());
        assertNull(result.getRadialVelocity());
        assertNull(result.getAstrophysicalParameters());

        assertEquals(1635721458409799680L, result.getSolutionId().longValue());
        assertEquals(1000274106300491264L, result.getSourceId().longValue());
        assertEquals(299262776L, result.getRandomIndex().longValue());
        assertEquals(23, result.getMatchedObservations().intValue());
        assertFalse(result.getDuplicated());
        assertSame(PhotMode.Gold, result.getPhotMode());
        assertSame(VarFlag.NotAvailable, result.getVarFlag());

        validateBasicAstrometry(
                result.getBasicAstrometry(),
                103.42475813355888,0.46460785796226267,
                56.4509029273674,0.5824898440349664,
                0.58187467f,
                159.71211027075057,22.635988527030435,
                98.84322130615392,33.42300676008159);

        validateFullAstrometry(
                result.getFullAstrometry(),
                0.3140346813433826,0.8946739045107119,
                -4.521304219905333,0.7892547578692352,
                -5.547878990107712,0.6746814174483251,
                -0.5039091f,-0.058437377f,0.29087126f,
                -0.76784647f,-0.29065016f,0.40313807f,
                0.5471358f,-0.18813604f,
                0.25174227f);

        validateAstroMeta(
                result.getAstroMeta(),
                195,0,
                194,1,
                1.0383844f,209.20168f,
                0.7125994972288285,0.420175769325166,
                false,0.08765993f,
                1.6282012495164278,0.11059723635043638,
                -0.07951875f,
                22,12,
                0.9518703f,
                RotatorObjectType.NotUsed);

        validateBandPhot(
                result.getGPhot(),
                203,
                214.12084454426326,1.04554734852966,
                19.86172f);

        validateBandPhot(
                result.getBpPhot(),
                21,
                57.23838701483902,6.706502845315356,
                20.95717f);

        validateBandPhot(
                result.getRpPhot(),
                21,
                235.7701129601971,5.291044212080072,
                18.830698f);

        validateColourInfo(
                result.getColourInfo(),
                1.3684258f,
                2.1264725f,
                1.0954514f,1.0310211f);
    }

    @Test
    void completeSourceInformationIsCorrectlyParsed() {
        final GaiaSource result = interpreter.apply(lines.get(Item.Everything.ordinal()));

        // Basic validation
        validateMandatory(result);
        assertNotNull(result.getFullAstrometry());
        assertNotNull(result.getBpPhot());
        assertNotNull(result.getRpPhot());
        assertNotNull(result.getColourInfo());
        assertNotNull(result.getRadialVelocity());
        assertNotNull(result.getAstrophysicalParameters());


        assertEquals(1635721458409799680L, result.getSolutionId().longValue());
        assertEquals(1000313035883659264L, result.getSourceId().longValue());
        assertEquals(1621259592L, result.getRandomIndex().longValue());
        assertEquals(24, result.getMatchedObservations().intValue());
        assertTrue(result.getDuplicated());
        assertSame(PhotMode.Gold, result.getPhotMode());
        assertSame(VarFlag.NotAvailable, result.getVarFlag());

        validateBasicAstrometry(
                result.getBasicAstrometry(),
                102.30172541443049, 0.027969076294376147,
                56.6658301006176, 0.028076461329161043,
                0.26536676f,
                159.3066295991376, 22.098771866133543,
                98.07790832184969, 33.570996566730436);

        validateFullAstrometry(
                result.getFullAstrometry(),
                2.9814674593114043,0.044120119152228555,
                0.2636609818189455,0.056405420285075766,
                -16.059379508324927,0.04678197904796344,
                -0.10145401f,-0.008310032f,-0.039388977f,
                -0.3477199f,-0.18023138f,0.15590502f,
                0.5088389f,0.08588405f,
                0.38546017f);

        validateAstroMeta(
                result.getAstroMeta(),
                204,186,
                196,8,
                2.594986f,245.55124f,
                0.0,0.0,
                true,237.86394f,
                1.598674001293753,0.008477076889491845,
                -0.11515785f,
                23,12,
                0.041336767f,
                RotatorObjectType.NotUsed);

        validateBandPhot(
                result.getGPhot(),
                197,
                171335.3986818583,36.87771848335473,
                12.603748f);

        validateBandPhot(
                result.getBpPhot(),
                21,
                93856.94482936406,227.03422789031578,
                12.920222f);

        validateBandPhot(
                result.getRpPhot(),
                22,
                118743.3724513135,338.5038163323732,
                12.075397f);

        validateColourInfo(
                result.getColourInfo(),
                1.2408429f,
                0.84482574f,0.31647396f,0.5283518f);

        validateRvs(
                result.getRadialVelocity(),
                -7.458119202037519,2.9143913701893007,
                3,
                5000.0f,3.0f,-1.5f);

        validateAstroParams(
                result.getAstrophysicalParameters(),
                100001, 200111,
                5730.9053f,5591.8735f,6048.26f,
                0.0298f,0.0082f,0.0743f,
                0.013f,0.0049f,0.0461f,
                0.8863408f,0.7957678f,0.9309632f,
                0.763465f,0.74693674f,0.77999324f);
    }

    @Test
    void minimalSourceIsCorrectlyParsed() {
        final GaiaSource result = interpreter.apply(lines.get(Item.Minimal.ordinal()));

        // Basic validation
        validateMandatory(result);
        assertNull(result.getFullAstrometry());
        assertNull(result.getBpPhot());
        assertNull(result.getRpPhot());
        assertNull(result.getColourInfo().getBpRp());
        assertNull(result.getColourInfo().getBpRpExcessFactor());
        assertNull(result.getColourInfo().getBpG());
        assertNull(result.getColourInfo().getRpG());
        assertNull(result.getRadialVelocity());
        assertNull(result.getAstrophysicalParameters());

        assertEquals(1635721458409799680L, result.getSolutionId().longValue());
        assertEquals(1000236280523298944L, result.getSourceId().longValue());
        assertEquals(223742503L, result.getRandomIndex().longValue());
        assertEquals(7, result.getMatchedObservations().intValue());
        assertFalse(result.getDuplicated());
        assertSame(PhotMode.Silver, result.getPhotMode());
        assertSame(VarFlag.NotAvailable, result.getVarFlag());

        validateBasicAstrometry(
                result.getBasicAstrometry(),
                103.44554963113235,7.510137996663214,
                56.29543043356949,5.682486151282338,
                -0.9622307f,
                159.87780720021408,22.60581220806461,
                98.8775108301353,33.26976258104981);

        validateAstroMeta(
                result.getAstroMeta(),
                57,0,
                57,0,
                0.24744375f,53.87896f,
                0.0,0.0,
                false,0.024742303f,
                Double.NaN, Double.NaN,
                -0.15526396f,
                7,
                6,19.2574f,
                RotatorObjectType.NotUsed);

        validateBandPhot(
                result.getGPhot(),
                60,
                72.88379575400086,1.3524330788864587,
                21.031788f);
    }

    private void validateBasicAstrometry(
            final CoreAstro result,
            final double ra, final double raErr,
            final double dec, final double decErr,
            final float raDecCorrelation,
            final double longGal, final double latGal,
            final double longEcl, final double latEcl) {
        assertNotNull(result);
        assertEquals(2015.5, result.getRefEpoch(), 1E-30);
        assertEquals(ra, result.getRa().getValue(), 1E-30);
        assertEquals(raErr, result.getRa().getUncertainty(), 1E-30);
        assertEquals(dec, result.getDec().getValue(), 1E-30);
        assertEquals(decErr, result.getDec().getUncertainty(), 1E-30);
        assertEquals(raDecCorrelation, result.getCorrelationRaDec(), 1E-5f);
        assertEquals(longGal, result.getLongGal(), 1E-30);
        assertEquals(latGal, result.getLatGal(), 1E-30);
        assertEquals(longEcl, result.getLongEcl(), 1E-30);
        assertEquals(latEcl, result.getLatEcl(), 1E-30);
    }

    private void validateFullAstrometry(
            final OptAstro result,
            final double parallax, final double parallaxError,
            final double pmRa, final double pmRaError,
            final double pmDec, final double pmDecError,
            final float correlationRaParallax, final float correlationRaPmRa, final float correlationRaPmDec,
            final float correlationDecParallax, final float correlationDecPmRa, final float correlationDecPmDec,
            final float correlationParallaxPmRa, final float correlationParallaxPmDec,
            final float correlationPmRaPmDec) {
        assertNotNull(result);
        assertEquals(parallax, parallaxError, result.getParallax());
        assertEquals(pmRa, pmRaError, result.getPmRa());
        assertEquals(pmDec, pmDecError, result.getPmDec());
        assertEquals(correlationRaParallax, result.getCorrelationRaParallax(), 1E-5f);
        assertEquals(correlationRaPmRa, result.getCorrelationRaPmRa(), 1E-5f);
        assertEquals(correlationRaPmDec, result.getCorrelationRaPmDec(), 1E-5f);
        assertEquals(correlationDecParallax, result.getCorrelationDecParallax(), 1E-5f);
        assertEquals(correlationDecPmRa, result.getCorrelationDecPmRa(), 1E-5f);
        assertEquals(correlationDecPmDec, result.getCorrelationDecPmDec(), 1E-5f);
        assertEquals(correlationParallaxPmRa, result.getCorrelationParallaxPmRa(), 1E-5f);
        assertEquals(correlationParallaxPmDec, result.getCorrelationParallaxPmDec(), 1E-5f);
        assertEquals(correlationPmRaPmDec, result.getCorrelationPmRaPmDec(), 1E-5f);
    }

    private void validateAstroMeta(
            final AstrometryMetadata result,
            final int numObsAl, final int numObsAc,
            final int numGoodObsAl, final int numBadObsAl,
            final float gofAl,
            final float chi2Al,
            final double excessNoise, final double excessNoiseSignificance,
            final boolean isPrimary,
            final float weightAl,
            final double pseudoColour, final double pseudoColourError,
            final float varpiFactorAl,
            final int matchedObservations,
            final int visibilityPeriodsUsed,
            final float sigma5dMax,
            final RotatorObjectType rotObjType) {
        assertNotNull(result);
        assertEquals(numObsAl, result.getNumObsAl().intValue());
        assertEquals(numObsAc, result.getNumObsAc().intValue());
        assertEquals(numGoodObsAl, result.getNumGoodObsAl().intValue());
        assertEquals(numBadObsAl, result.getNumBadObsAl().intValue());
        assertEquals(gofAl, result.getGofAl(), 1E-5f);
        assertEquals(chi2Al, result.getChi2Al(), 1E-5f);
        assertEquals(excessNoise, result.getExcessNoise(), 1E-5f);
        assertEquals(excessNoiseSignificance, result.getExcessNoiseSignificance(), 1E-5);
        assertEquals(isPrimary, result.getIsPrimary());
        assertEquals(weightAl, result.getWeightAl(), 1E-5f);
        if (Double.isFinite(pseudoColour)) {
            assertEquals(pseudoColour, pseudoColourError, result.getPseudoColour());
        } else {
            assertNull(result.getPseudoColour());
        }
        assertEquals(varpiFactorAl, result.getVarpiFactorAl(), 1E-5f);
        assertEquals(matchedObservations, result.getMatchedObservations().intValue());
        assertEquals(visibilityPeriodsUsed, result.getVisibilityPeriodsUsed().intValue());
        assertEquals(sigma5dMax, result.getSigma5dMax(), 1E-5f);
        assertSame(rotObjType, result.getRotObjType());
    }

    private void validateBandPhot(
            final BandPhot result,
            final int numObs,
            final double flux, final double fluxError,
            final float mag) {
        assertNotNull(result);
        assertEquals(numObs, result.getNumObs().intValue());
        assertEquals(flux, fluxError, result.getFlux());
        assertEquals(mag, result.getMag(), 1E-5f);
    }

    private void validateColourInfo(
            final ColourInfo result,
            final float xpExcess,
            final float bp_rp,
            final float bp_g,
            final float rp_g) {
        assertNotNull(result);
        assertFloat(xpExcess, result.getBpRpExcessFactor());
        assertFloat(bp_rp, result.getBpRp());
        assertFloat(bp_g, result.getBpG());
        assertFloat(rp_g, result.getRpG());
    }

    private void validateRvs(
            final Rvs result,
            final double radVel, final double radVelErr,
            final int nobs,
            final float tplTeff, final float tplLogg, final float tplFeh) {
        assertNotNull(result);
        assertEquals(radVel, radVelErr, result.getRadVel());
        assertEquals(nobs, result.getTransitUsed().intValue());
        assertEquals(tplTeff, result.getTemplateTeff(), 1E-5f);
        assertEquals(tplLogg, result.getTemplateLogg(), 1E-5f);
        assertEquals(tplFeh, result.getTemplateFeH(), 1E-5f);
    }

    private void validateAstroParams(
            final AstroParams result,
            final long flag,
            final long flamesFlag,
            final float teff, final float teffLow, final float teffUp,
            final float ext, final float extLow, final float extUp,
            final float red, final float redLow, final float redUp,
            final float radius, final float radiusLow, final float radiusUp,
            final float lum, final float lumLow, final float lumUp) {
        assertNotNull(result);
        assertEquals(flag, result.getPriamFlags().longValue());
        assertEquals(flamesFlag, result.getFlameFlags().longValue());
        assertEquals(teff, teffLow, teffUp, result.getEffectiveTemperature());
        assertEquals(ext, extLow, extUp, result.getExtinction());
        assertEquals(red, redLow, redUp, result.getReddening());
        assertEquals(radius, radiusLow, radiusUp, result.getRadius());
        assertEquals(lum, lumLow, lumUp, result.getLuminosity());
    }

    private void validateMandatory(final GaiaSource result) {
        assertNotNull(result);
        assertNotNull(result.getBasicAstrometry());
        assertNotNull(result.getGPhot());
        assertNotNull(result.getAstroMeta());
    }
}
