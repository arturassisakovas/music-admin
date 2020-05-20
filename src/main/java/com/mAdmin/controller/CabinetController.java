package com.mAdmin.controller;

import com.mAdmin.repository.GroupRepository;
import com.mAdmin.repository.PoolRepository;
import com.mAdmin.repository.CabinetPeriodRepository;
import com.mAdmin.repository.CabinetRepository;
import com.mAdmin.component.MessageBundleComponent;
import com.mAdmin.entity.Pool;
import com.mAdmin.entity.Track;
import com.mAdmin.entity.TrackPeriod;
import com.mAdmin.model.CabinetLazyDataModel;
import com.mAdmin.util.PrimeFacesWrapper;
import org.primefaces.PrimeFaces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.util.List;


@Controller
@Scope(value = "view")
public class CabinetController {

    
    private Track track;

    
    private Pool pool;

    
    @Autowired
    private CabinetRepository cabinetRepository;

    
    @Autowired
    private CabinetPeriodRepository cabinetPeriodRepository;

    
    @Autowired
    private PoolRepository poolRepository;

    
    @Autowired
    private MessageBundleComponent messageBundleComponent;

    
    @Autowired
    private GroupRepository groupRepository;

    
    @Autowired
    private PrimeFacesWrapper primeFacesWrapper;

    
    private List<Pool> pools;

    
    private List<Track> tracks;

    
    private int index;

    
    private boolean create;

    
    @Autowired
    private CabinetLazyDataModel model;

    
    @Value("${paginator.rows}")
    private int rowsPerPage;

    
    @Value("${paginator.rowsPerPageTemplate}")
    private String rowsPerPageTemplate;

    
    @PostConstruct
    public void init() {
        tracks = cabinetRepository.findAll();
        track = new Track();
    }

    
    public void save() {
        FacesContext context = FacesContext.getCurrentInstance();
        track.setNumber(track.getNumber());
        track.setPool(pool);

        boolean duplicated = false;

        List<Track> tracksByNameList = cabinetRepository.findAllByNumber(track.getNumber());

        if (tracksByNameList != null && create) {
            for (Track trackFromList : tracksByNameList) {
                if (trackFromList.getPool().getId().equals(pool.getId())) {
                    duplicated = true;
                    messageBundleComponent.generateMessage(FacesMessage.SEVERITY_ERROR,
                            "admin.track.duplicated", context);
                    break;
                }
            }
        } else if (tracksByNameList != null) {
            for (Track trackFromList : tracksByNameList) {
                if (trackFromList.getPool().getId().equals(pool.getId()) && !trackFromList.getId().equals(track.getId())) {

                    duplicated = true;
                    messageBundleComponent.generateMessage(FacesMessage.SEVERITY_ERROR,
                            "admin.track.duplicated", context);
                    break;
                }
            }
        }

        if (!create && !duplicated) {
            tracks.remove(index);
            tracks.add(index, cabinetRepository.saveAndFlush(track));
            messageBundleComponent.generateMessage(FacesMessage.SEVERITY_WARN, "admin.track.updated.success", context);
        } else if (create && !duplicated) {
            tracks.add(cabinetRepository.saveAndFlush(track));
            messageBundleComponent.generateMessage(FacesMessage.SEVERITY_INFO, "admin.track.created.success", context);
        }

        if (!duplicated) {
            PrimeFaces current = primeFacesWrapper.current();
            current.executeScript("PF('trackDialog').hide();");
        }

    }

    
    public void edit(Track track, int index) {
        this.index = index;
        this.track = track;
        poolRepository.findById(track.getPool().getId()).ifPresent(value -> this.pool = value);
        setCreate(false);
    }

    
    public void add() {
        track = new Track();
        pool = null;
        pools = poolRepository.findAll();
        setCreate(true);
    }

    
    public void delete(Long id, int index) {

            cabinetRepository.deleteById(id);
            tracks.remove(index);
            messageBundleComponent.generateMessage(FacesMessage.SEVERITY_INFO, "admin.track.deleted.success",
                    FacesContext.getCurrentInstance());
    }

    
    public boolean canTrackBeEditable(Track track) {
        List<TrackPeriod> trackPeriods = cabinetPeriodRepository.findByTrack(track);
        return !groupRepository.findByTrackPeriodInAndIsPublicTrue(trackPeriods).isEmpty();
    }

    
    public boolean checkIfTrackHasTrackPeriod(Long id) {
        return cabinetPeriodRepository.findFirstByTrackId(id).isPresent();
    }

    
    public Track getTrack() {
        return track;
    }

    
    public void setTrack(Track track) {
        this.track = track;
    }

    
    public List<Track> getTracks() {
        return tracks;
    }

    
    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
    }

    
    public List<Pool> getPools() {
        return pools;
    }

    
    public void setPools(List<Pool> pools) {
        this.pools = pools;
    }

    
    public Pool getPool() {
        return pool;
    }

    
    public void setPool(Pool pool) {
        this.pool = pool;
    }

    
    public boolean isCreate() {
        return create;
    }

    
    public void setCreate(boolean create) {
        this.create = create;
    }

    
    public CabinetLazyDataModel getModel() {
        return model;
    }

    
    public void setModel(CabinetLazyDataModel model) {
        this.model = model;
    }

    
    public int getRowsPerPage() {
        return rowsPerPage;
    }

    
    public String getRowsPerPageTemplate() {
        return rowsPerPageTemplate;
    }
}
