package com.webank.wedatasphere.dss.data.common.atlas;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.atlas.model.PList;
import org.apache.atlas.model.SearchFilter;
import org.apache.atlas.model.instance.AtlasClassification;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.PUBLIC_ONLY;

public class AtlasClassificationV2 extends AtlasClassification {
    // org.codehaus.jackson.map.JsonMappingException: Conflicting getter definitions for property "propagate"
    // https://stackoverflow.com/questions/20624891/stuck-on-org-codehaus-jackson-map-jsonmappingexception-conflicting-getter-defi
    @Override
    @JsonIgnore
    public Boolean isPropagate() {
        return super.getPropagate();
    }

    /**
     * REST serialization friendly list.
     */
    @JsonAutoDetect(getterVisibility=PUBLIC_ONLY, setterVisibility=PUBLIC_ONLY, fieldVisibility=NONE)
    @JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown=true)
    @XmlRootElement
    @XmlAccessorType(XmlAccessType.PROPERTY)
    @XmlSeeAlso(AtlasClassification.class)
    public static class AtlasClassificationsV2 extends PList<AtlasClassificationV2> {
        private static final long serialVersionUID = 1L;

        public AtlasClassificationsV2() {
            super();
        }

        public AtlasClassificationsV2(List<AtlasClassificationV2> list) {
            super(list);
        }

        public AtlasClassificationsV2(List list, long startIndex, int pageSize, long totalCount,
                                    SearchFilter.SortType sortType, String sortBy) {
            super(list, startIndex, pageSize, totalCount, sortType, sortBy);
        }
    }
}
