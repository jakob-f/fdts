package at.frohnwieser.mahut.webapp.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import at.frohnwieser.mahut.webapp.util.SessionUtils;
import at.frohnwieser.mahut.webapp.util.Value;
import at.frohnwieser.mahut.webappapi.db.manager.AssetManager;
import at.frohnwieser.mahut.webappapi.db.manager.HashTagManager;
import at.frohnwieser.mahut.webappapi.db.manager.SetManager;
import at.frohnwieser.mahut.webappapi.db.model.Asset;
import at.frohnwieser.mahut.webappapi.db.model.HashTag;
import at.frohnwieser.mahut.webappapi.db.model.Set;
import at.frohnwieser.mahut.webappapi.db.model.User;

@SuppressWarnings("serial")
@RequestScoped
@Named
public class HashTagsController extends AbstractDBObjectController<HashTag> {

    @SuppressWarnings("unchecked")
    @Override
    protected HashTagManager _managerInstance() {
	return HashTagManager.getInstance();
    }

    @Nonnull
    public List<String> completeQuery(@Nullable final String sQuery) {
	final List<String> aRet = new ArrayList<String>();

	if (StringUtils.isNotEmpty(sQuery))
	    getAll().stream().filter(aHashTag -> aHashTag.getTag().contains(sQuery.toLowerCase())).forEach(aHashTag -> aRet.add(aHashTag.getTag()));

	return aRet;

	// TODO WTF this does not work...
	// return getAll().stream().filter(aHashTag ->
	// aHashTag.getTag().contains(sQuery.toLowerCase())).map(aHashTag ->
	// aHashTag.getTag())
	// .collect(Collectors.toCollection(ArrayList::new));
    }

    @Nonnull
    private Collection<String> _getFromParameterTags() {
	final String sRequestParameter = SessionUtils.getInstance().getRequestParameter(Value.REQUEST_PARAMETER_SEARCH);

	if (StringUtils.isNotEmpty(sRequestParameter))
	    return Arrays.asList(sRequestParameter.split(" "));

	return new ArrayList<String>();
    }

    @Nonnull
    private Collection<HashTag> _getFromParameterHashTags() {
	return _managerInstance().get(_getFromParameterTags());
    }

    @Nullable
    public Collection<Asset> getFromParamterAssets() {
	final Collection<HashTag> aHashTags = _getFromParameterHashTags();

	if (!aHashTags.isEmpty()) {
	    final HashTag aMinHashTag = aHashTags.stream().min((aHashTag1, aHashTag2) -> (aHashTag1.getAssetIds().size() - aHashTag2.getAssetIds().size()))
		    .get();
	    final Collection<String> aAssetIds = new HashSet<String>(aMinHashTag.getAssetIds());

	    aHashTags.forEach(aHashTag -> aMinHashTag.getAssetIds().stream().filter(nAssetId -> !aHashTag.getAssetIds().contains(nAssetId))
		    .forEach(nAssetId -> aAssetIds.remove(nAssetId)));

	    final User aUser = SessionUtils.getInstance().getLoggedInUser();
	    final ArrayList<Asset> aAssets = aAssetIds.stream().map(nAssetId -> AssetManager.getInstance().getRead(aUser, nAssetId)).filter(o -> o != null)
		    .collect(Collectors.toCollection(ArrayList::new));
	    Collections.sort(aAssets);

	    return aAssets;
	}

	return null;
    }

    @Nullable
    public Collection<HashTag> getFromParamterOthers() {
	return _managerInstance().getOthers(_getFromParameterTags());
    }

    @Nonnull
    public Collection<Set> getFromParamterSets() {
	final User aUser = SessionUtils.getInstance().getLoggedInUser();
	final Collection<HashTag> aHashTags = _getFromParameterHashTags();

	if (CollectionUtils.isNotEmpty(aHashTags)) {
	    final HashTag aMinHashTag = aHashTags.stream().min((aHashTag1, aHashTag2) -> (aHashTag1.getSetIds().size() - aHashTag2.getSetIds().size())).get();
	    final Collection<String> aSetIds = new HashSet<String>(aMinHashTag.getSetIds());

	    aHashTags.forEach(aHashTag -> aMinHashTag.getSetIds().stream().filter(nSetId -> !aHashTag.getSetIds().contains(nSetId))
		    .forEach(nSetId -> aSetIds.remove(nSetId)));

	    final ArrayList<Set> aSets = aSetIds.stream().map(nSetId -> SetManager.getInstance().getRead(aUser, nSetId)).filter(o -> o != null)
		    .collect(Collectors.toCollection(ArrayList::new));
	    Collections.sort(aSets);

	    return aSets;
	}
	// TODO return on empty search SetManager.getInstance().allRead(aUser);?

	return new ArrayList<Set>();
    }
}
