package at.ac.tuwien.media.master.webappui.controller;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.commons.lang3.StringUtils;

import at.ac.tuwien.media.master.webapp.util.Value;
import at.ac.tuwien.media.master.webappapi.db.manager.impl.SetManager;
import at.ac.tuwien.media.master.webappapi.db.model.Set;

@SuppressWarnings("serial")
@SessionScoped
@ManagedBean(name = Value.CONTROLLER_SETS)
public class SetsController extends AbstractDBObjectController<Set> {

    @SuppressWarnings("unchecked")
    @Override
    protected SetManager _managerInstance() {
	return SetManager.getInstance();
    }

    @Override
    protected boolean _validateEntry(@Nullable final Set aEntry) {
	return aEntry != null && StringUtils.isNoneEmpty(aEntry.getName()) && StringUtils.isNoneEmpty(aEntry.getMetaContent());
    }

    @Override
    @Nonnull
    protected Set _new() {
	return new Set();
    }

    public Set getParent() {
	return SetManager.getInstance().getParent(getEntry());
    }
}
