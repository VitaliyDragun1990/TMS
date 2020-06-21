package org.vdragun.tms.config.thymeleaf;

import static java.lang.String.format;

/**
 * @author Vitaliy Dragun
 *
 */
public class ThymeleafUtil {

    private static final String ASC = "asc";
    private static final String DESC = "desc";

    private static final String SORT_TMP = "sort=%s,%s";
    private static final String PAGE_TMP = "page=%d";

    /**
     * Updates pagination info in provided URI. If given URI doesn't contain page
     * query parameter, it will be added, if page query parameter already present,
     * it will be updated with specified value. Also sets provided sorted keys using
     * next algorithm: if no such sort key present (as query parameter) then new
     * query parameter will be added with value: 'sort=sortKey,asc'. If such sort
     * key query parameter already present, its value will be updated using next
     * algorithm: asc -> desc/desc -> asc.
     * 
     * @param uri      request URI to update
     * @param pageNum  page number to set
     * @param sortKeys optional sort keys to set
     */
    public String updatePagingInfo(String uri, int pageNum, String... sortKeys) {
        String result = prepareToUpdate(uri);

        result = updatePageParam(pageNum, result);

        result = updateSortParams(result, sortKeys);

        return result;
    }

    private String prepareToUpdate(String uri) {
        // add '?' if URI doesn't have one yet
        if (uri.indexOf('?') == -1) {
            return (uri + "?");
        }
        return uri;
    }

    private String updateSortParams(String uri, String... sortKeys) {
        String result = uri;

        // add/update sortKeys parameters
        for (String sortKey : sortKeys) {

            // add/update sort parameter
            if (result.contains(format("sort=%s", sortKey))) {
                // if contains ASC -> change to DESC
                if (result.contains(format(SORT_TMP, sortKey, ASC))) {
                    result = result.replaceFirst(format(SORT_TMP, sortKey, ASC), format(SORT_TMP, sortKey, DESC));
                } else {
                    // else replace DESC to ASC
                    result = result.replaceFirst(format(SORT_TMP, sortKey, DESC), format(SORT_TMP, sortKey, ASC));
                }
            } else {
                if (result.endsWith("?")) {
                    result += format(SORT_TMP, sortKey, ASC);
                } else {
                    result += ("&" + format(SORT_TMP, sortKey, ASC));
                }
            }
        }

        return result;
    }

    private String updatePageParam(int pageNum, String uri) {
        String result = uri;

        // add/update page query parameter
        if (result.indexOf("page=") == -1) {
            if (result.endsWith("?")) {
                result += format(PAGE_TMP, pageNum);
            } else {
                result += ("&" + format(PAGE_TMP, pageNum));
            }
        } else {
            result = result.replaceFirst("page=\\d+", format(PAGE_TMP, pageNum));
        }

        return result;
    }

}
