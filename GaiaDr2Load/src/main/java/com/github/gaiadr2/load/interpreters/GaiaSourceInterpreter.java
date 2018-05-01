package com.github.gaiadr2.load.interpreters;

import com.github.gaiadr2.avro.common.Dval;
import com.github.gaiadr2.avro.source.AstroPar;
import com.github.gaiadr2.avro.source.AstroParamSolved;
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
import com.github.gaiadr2.load.csv.specs.GaiaSourceSpec;

import java.util.OptionalDouble;
import java.util.OptionalLong;
import java.util.function.Function;

/**
 * Interpreter for the CSV representation of the {@code gaia_source} Gaia DR2 archive table
 */
public final class GaiaSourceInterpreter extends AnyInterpreter<GaiaSourceSpec> implements Function<String, GaiaSource> {

    public GaiaSourceInterpreter() {
        super(GaiaSourceSpec.class);
    }

    @Override
    public GaiaSource apply(final String csvLine) {
        interpreter.accept(csvLine);
        return new GaiaSource(
                getLong(GaiaSourceSpec.SolutionId),
                getLong(GaiaSourceSpec.SourceId),
                getLong(GaiaSourceSpec.RandomIndex),
                coreAstrometry(),
                fullAstrometry(),
                astrometryMetadata(),
                getInt(GaiaSourceSpec.MatchedObservations),
                getBoolean(GaiaSourceSpec.DuplicatedSource),
                gbandPhot(),
                bpPhot(),
                rpPhot(),
                colourInfo(),
                photMode(getInt(GaiaSourceSpec.PhotProcMode)),
                variFlag(getRaw(GaiaSourceSpec.PhotVariableFlag)),
                rvs(),
                astrophysicalParameters());
    }

    /**
     * Generate the required {@link CoreAstro} component
     *
     * @return The interpreted and fully populated component
     */
    private CoreAstro coreAstrometry() {
        return new CoreAstro(
                getDouble(GaiaSourceSpec.RefEpoch),
                new Dval(
                        getDouble(GaiaSourceSpec.Ra),
                        getDouble(GaiaSourceSpec.RaError)),
                new Dval(
                        getDouble(GaiaSourceSpec.Dec),
                        getDouble(GaiaSourceSpec.DecError)),
                getFloat(GaiaSourceSpec.RaDecCorr),
                getDouble(GaiaSourceSpec.LongitudeGalactic),
                getDouble(GaiaSourceSpec.LatitudeGalactic),
                getDouble(GaiaSourceSpec.LongitudeEcliptic),
                getDouble(GaiaSourceSpec.LatitudeEcliptic));
    }

    /**
     * Generate the optional {@link OptAstro} component
     *
     * @return The interpreted component, when available, {@code null} otherwise
     */
    private OptAstro fullAstrometry() {
        final OptionalDouble varPi = interpreter.doubleValue(GaiaSourceSpec.Parallax);
        if (varPi.isPresent()) {
            return new OptAstro(
                    new Dval(
                            varPi.getAsDouble(),
                            getDouble(GaiaSourceSpec.ParallaxError)),
                    new Dval(
                            getDouble(GaiaSourceSpec.PmRa),
                            getDouble(GaiaSourceSpec.PmRaError)),
                    new Dval(
                            getDouble(GaiaSourceSpec.PmDec),
                            getDouble(GaiaSourceSpec.PmDecError)),
                    getFloat(GaiaSourceSpec.RaParallaxCorr),
                    getFloat(GaiaSourceSpec.RaPmRaCorr),
                    getFloat(GaiaSourceSpec.RaPmDecCorr),
                    getFloat(GaiaSourceSpec.DecParallaxCorr),
                    getFloat(GaiaSourceSpec.DecPmRaCorr),
                    getFloat(GaiaSourceSpec.DecPmDecCorr),
                    getFloat(GaiaSourceSpec.ParallaxPmRaCorr),
                    getFloat(GaiaSourceSpec.ParallaxPmDecCorr),
                    getFloat(GaiaSourceSpec.PmRaPmDecCorr));
        }
        return null;
    }

    /**
     * Generate the required {@link AstrometryMetadata} component
     *
     * @return The interpreted and fully populated component
     */
    private AstrometryMetadata astrometryMetadata() {
        final Dval pseudoColour = interpreter.rawValue(GaiaSourceSpec.AstroPseudoColour).isPresent() ?
                new Dval(
                        getDouble(GaiaSourceSpec.AstroPseudoColour),
                        getDouble(GaiaSourceSpec.AstroPseudoColourError)) :
                null;

        return new AstrometryMetadata(
                getInt(GaiaSourceSpec.AstroNobsAl),
                getInt(GaiaSourceSpec.AstroNobsAc),
                getInt(GaiaSourceSpec.AstroNobsGoodAl),
                getInt(GaiaSourceSpec.AstroNobsBadAl),
                getFloat(GaiaSourceSpec.AstroGofAl),
                getFloat(GaiaSourceSpec.AstroChi2Al),
                getDouble(GaiaSourceSpec.AstroExcessNoise),
                getDouble(GaiaSourceSpec.AstroExcessNoiseSig),
                astroParamSolved(getInt(GaiaSourceSpec.AstroParamSolved)),
                getBoolean(GaiaSourceSpec.AstroPrimaryFlag),
                getFloat(GaiaSourceSpec.AstroWeightAl),
                pseudoColour,
                getFloat(GaiaSourceSpec.MeanVarPiFactor),
                getInt(GaiaSourceSpec.AstroMatchedObs),
                getInt(GaiaSourceSpec.VisibilityPeriodsUsed),
                getFloat(GaiaSourceSpec.AstroSigma5dMax),
                rotatorType(getInt(GaiaSourceSpec.FrameRotatorObjType)));
    }

    /**
     * Generate the required {@link BandPhot} component for the G band
     *
     * @return The interpreted and fully populated component
     */
    private BandPhot gbandPhot() {
        return new BandPhot(
                getInt(GaiaSourceSpec.PhotGNobs),
                new Dval(
                        getDouble(GaiaSourceSpec.PhotGMeanFlux),
                        getDouble(GaiaSourceSpec.PhotGMeanFluxError)),
                getFloat(GaiaSourceSpec.PhotGMeanMag));
    }

    /**
     * Generate the optional {@link BandPhot} component for BP
     *
     * @return The interpreted component, when available, {@code null} otherwise
     */
    private BandPhot bpPhot() {
        final int numBp = getInt(GaiaSourceSpec.PhotBpNobs);
        if (numBp > 0) {
            return new BandPhot(
                    numBp,
                    new Dval(
                            getDouble(GaiaSourceSpec.PhotBpMeanFlux),
                            getDouble(GaiaSourceSpec.PhotBpMeanFluxError)),
                    getFloat(GaiaSourceSpec.PhotBpMeanMag));
        }
        return null;
    }

    /**
     * Generate the optional {@link BandPhot} component for RP
     *
     * @return The interpreted component, when available, {@code null} otherwise
     */
    private BandPhot rpPhot() {
        final int numRp = getInt(GaiaSourceSpec.PhotRpNobs);
        if (numRp > 0) {
            return new BandPhot(
                    numRp,
                    new Dval(
                            getDouble(GaiaSourceSpec.PhotRpMeanFlux),
                            getDouble(GaiaSourceSpec.PhotRpMeanFluxError)),
                    getFloat(GaiaSourceSpec.PhotRpMeanMag));
        }
        return null;
    }

    /**
     * Generate the required component {@link ColourInfo}
     *
     * @return The interpreted component providing the available information
     */
    private ColourInfo colourInfo() {
        return new ColourInfo(
                interpreter.floatValue(GaiaSourceSpec.PhotBpRpExcessFactor).orElse(null),
                interpreter.floatValue(GaiaSourceSpec.ColourBpRp).orElse(null),
                interpreter.floatValue(GaiaSourceSpec.ColourBpG).orElse(null),
                interpreter.floatValue(GaiaSourceSpec.ColourGRp).orElse(null));
    }

    /**
     * Generate the optional {@link Rvs} component
     *
     * @return The interpreted component, when available, {@code null} otherwise
     */
    private Rvs rvs() {
        final int nobs = getInt(GaiaSourceSpec.RvsNbTransits);
        if (nobs > 0) {
            return new Rvs(
                    new Dval(
                            getDouble(GaiaSourceSpec.RadialVelocity),
                            getDouble(GaiaSourceSpec.RadialVelocityError)),
                    nobs,
                    getFloat(GaiaSourceSpec.RvsTemplateTeff),
                    getFloat(GaiaSourceSpec.RvsTemplateLogg),
                    getFloat(GaiaSourceSpec.RvsTemplateFeh));
        }
        return null;
    }

    private AstroParams astrophysicalParameters() {
        final OptionalLong flag = interpreter.longValue(GaiaSourceSpec.ParamsFlags);
        if (flag.isPresent()) {
            return new AstroParams(
                    flag.getAsLong(),
                    getLong(GaiaSourceSpec.ParamsFlameFlags),
                    new AstroPar(
                            getFloat(GaiaSourceSpec.ParamsTeffValue),
                            getFloat(GaiaSourceSpec.ParamsTeffPercLower),
                            getFloat(GaiaSourceSpec.ParamsTeffPercUpper)),
                    new AstroPar(
                            getFloat(GaiaSourceSpec.ParamsExtinctionG),
                            getFloat(GaiaSourceSpec.ParamsExtinctionGPercLower),
                            getFloat(GaiaSourceSpec.ParamsExtinctionGPercUpper)),
                    new AstroPar(
                            getFloat(GaiaSourceSpec.ParamsReddening),
                            getFloat(GaiaSourceSpec.ParamsReddeningPercLower),
                            getFloat(GaiaSourceSpec.ParamsReddeningPercUpper)),
                    new AstroPar(
                            getFloat(GaiaSourceSpec.ParamsRadius),
                            getFloat(GaiaSourceSpec.ParamsRadiusPercLower),
                            getFloat(GaiaSourceSpec.ParamsRadiusPercUpper)),
                    new AstroPar(
                            getFloat(GaiaSourceSpec.ParamsLuminosity),
                            getFloat(GaiaSourceSpec.ParamsLuminosityPercLower),
                            getFloat(GaiaSourceSpec.ParamsLuminosityPercUpper)));
        }
        return null;
    }

    /**
     * Generate the {@link AstroParamSolved} enum flag from the encoded value
     *
     * @param value Encoded value
     * @return Explicit flag
     */
    private AstroParamSolved astroParamSolved(final int value) {
        switch (value) {
            case 3:
                return AstroParamSolved.PositionOnly;
            case 31:
                return AstroParamSolved.Full;
            default:
                throw new IllegalArgumentException("Unknown value for AstroParamSolved: " + value);
        }
    }

    /**
     * Generate the {@link RotatorObjectType} enum flag from the encoded value
     *
     * @param value Encoded value
     * @return Explicit flag
     */
    private RotatorObjectType rotatorType(final int value) {
        return RotatorObjectType.values()[value];
    }

    /**
     * Generate the {@link PhotMode} enum flag from the encoded value
     *
     * @param value Encoded value
     * @return Explicit flag
     */
    private PhotMode photMode(final int value) {
        return PhotMode.values()[value];
    }

    /**
     * Generate the {@link VarFlag} enum flag from the encoded value
     *
     * @param value Encoded value
     * @return Explicit flag
     */
    private VarFlag variFlag(final String value) {
        switch (value) {
            case "NOT_AVAILABLE":
                return VarFlag.NotAvailable;
            case "CONSTANT":
                return VarFlag.Constant;
            case "VARIABLE":
                return VarFlag.Variable;
            default:
                throw new IllegalArgumentException("Unknown variability flag " + value);
        }
    }
}
