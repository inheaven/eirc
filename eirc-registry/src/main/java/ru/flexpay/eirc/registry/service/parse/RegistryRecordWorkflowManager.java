package ru.flexpay.eirc.registry.service.parse;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.flexpay.eirc.registry.entity.ImportErrorType;
import ru.flexpay.eirc.registry.entity.Registry;
import ru.flexpay.eirc.registry.entity.RegistryRecord;
import ru.flexpay.eirc.registry.entity.RegistryRecordStatus;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static ru.flexpay.eirc.registry.entity.RegistryRecordStatus.*;

/**
 * @author Pavel Sknar
 */
@Stateless
public class RegistryRecordWorkflowManager {

    private Logger log = LoggerFactory.getLogger(getClass());

    @EJB
    private RegistryWorkflowManager registryWorkflowManager;

    protected Map<RegistryRecordStatus, List<RegistryRecordStatus>> transitions = 
            ImmutableMap.of(LOADED,                 ImmutableList.of(PROCESSED, PROCESSED_WITH_ERROR),
                            PROCESSED_WITH_ERROR,   ImmutableList.of(FIXED),
                            FIXED,                  ImmutableList.of(PROCESSED, PROCESSED_WITH_ERROR),
                            PROCESSED,              Collections.<RegistryRecordStatus>emptyList());

    /**
     * Check if registry record processing allowed
     *
     * @param record	 Registry to check
     * @param nextStatus Registry status to set up
     * @return <code>true</code> if registry processing allowed, or <code>false</code> otherwise
     */
    public boolean canTransit(RegistryRecord record, RegistryRecordStatus nextStatus) {
        return transitions.get(record.getStatus()).contains(nextStatus);
    }

    public boolean hasSuccessTransition(RegistryRecord record) {
        return !transitions.get(record.getStatus()).isEmpty();
    }

    /**
     * Check if registry record is in state that allows processing, or moves it to an allowed one.
     *
     * @param record Registry record to start
     * @throws TransitionNotAllowed if record processing is not possible
     */
    public void startProcessing(RegistryRecord record) throws TransitionNotAllowed {
        if (!hasSuccessTransition(record)) {
            throw new TransitionNotAllowed("Registry processing not allowed");
        }

        if (record.getStatus() == PROCESSED_WITH_ERROR) {
            setNextSuccessStatus(record);
        }
    }

    /**
     * Set next error registry record status
     *
     * @param record Registry record to update
     * @throws TransitionNotAllowed if error transition is not allowed
     */
    public void setNextErrorStatus(RegistryRecord record, Registry registry) throws TransitionNotAllowed {

        if (record.getStatus() == PROCESSED_WITH_ERROR) {
            return;
        }

        List<RegistryRecordStatus> allowedCodes = transitions.get(record.getStatus());
        if (allowedCodes.size() < 2) {
            throw new TransitionNotAllowed("No error transition, current is", record.getStatus());
        }

        markRegistryAsHavingError(record, registry);
        setNextStatus(record, allowedCodes.get(1));
    }

    /**
     * Set next error registry record status and setup error
     *
     * @param record Registry record to update
     * @param importErrorType  ImportErrorType
     * @throws TransitionNotAllowed if error transition is not allowed
     */
    public void setNextErrorStatus(RegistryRecord record, Registry registry, ImportErrorType importErrorType) throws TransitionNotAllowed {
        List<RegistryRecordStatus> allowedCodes = transitions.get(record.getStatus());
        if (allowedCodes.size() < 2) {
            throw new TransitionNotAllowed("No error transition, current is: '", record.getStatus());
        }

        markRegistryAsHavingError(record, registry);
        setNextStatus(record, allowedCodes.get(1));
        record.setImportErrorType(importErrorType);
    }

    private void markRegistryAsHavingError(RegistryRecord record, Registry registry) throws TransitionNotAllowed {

        log.debug("Setting record errors: {}", record);

        registryWorkflowManager.markProcessingHasError(registry);
    }

    /**
     * Set next success registry record status
     *
     * @param record Registry record to update
     * @throws TransitionNotAllowed if success transition is not allowed
     */
    public void setNextSuccessStatus(RegistryRecord record) throws TransitionNotAllowed {
        List<RegistryRecordStatus> allowedCodes = transitions.get(record.getStatus());
        if (allowedCodes.size() < 1) {
            throw new TransitionNotAllowed("No success transition");
        }

        if (record.getStatus() == PROCESSED_WITH_ERROR) {
            record = removeError(record);
        }

        setNextStatus(record, allowedCodes.get(0));
    }

    /**
     * Set next success registry records status
     *
     * @param records Registry records to update
     * @throws TransitionNotAllowed
     *          if success transition is not allowed for some of the records
     */
    public void setNextSuccessStatus(Collection<RegistryRecord> records) throws TransitionNotAllowed {

        for (RegistryRecord record : records) {
            setNextSuccessStatus(record);
        }
    }

    public void setNextStatusForErrorRecords(Collection<RegistryRecord> records) throws TransitionNotAllowed {
        log.info("For this implementation this method not allowed");
    }

    /**
     * Set record status to fixed and invalidate error
     *
     * @param record Registry record
     * @return updated record
     */
    public RegistryRecord removeError(RegistryRecord record) {
        if (record.getImportErrorType() == null) {
            return record;
        }

        record.setImportErrorType(ImportErrorType.FIXED);

        return record;
    }

    /**
     * Set initial registry record status
     *
     * @param record Registry record to update
     * @return SpRegistryRecord back
     * @throws TransitionNotAllowed if registry already has a status
     */
    public RegistryRecord setInitialStatus(RegistryRecord record) throws TransitionNotAllowed {
        if (record.getStatus() != null) {
            if (record.getStatus() != LOADED) {
                throw new TransitionNotAllowed("Registry was already processed, cannot set initial status");
            }

            return record;
        }

        record.setStatus(LOADED);
        return record;
    }

    /**
     * Set next registry record status
     *
     * @param record Registry record to update
     * @param status Next status to set
     * @throws TransitionNotAllowed if transition from old to a new status is not allowed
     */
    public void setNextStatus(RegistryRecord record, RegistryRecordStatus status) throws TransitionNotAllowed {
        if (!canTransit(record, status)) {
            throw new TransitionNotAllowed("Invalid transition request, was " + record.getStatus() + ", requested " + status);
        }

        record.setStatus(status);
    }
}
