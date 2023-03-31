/*
 * Copyright (c) 2002-2023, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */

package fr.paris.lutece.plugins.htmlpage.modules.rest.rs;

import fr.paris.lutece.plugins.htmlpage.business.HtmlPage;
import fr.paris.lutece.plugins.htmlpage.modules.rest.util.AllowCorsOriginUtil;
import fr.paris.lutece.plugins.htmlpage.service.HtmlPageService;
import fr.paris.lutece.plugins.htmlpage.utils.HtmlPageUtil;
import fr.paris.lutece.plugins.rest.service.RestConstants;
import fr.paris.lutece.util.json.ErrorJsonResponse;
import fr.paris.lutece.util.json.JsonResponse;
import fr.paris.lutece.util.json.JsonUtil;
import fr.paris.lutece.portal.service.util.AppLogService;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

/**
 * HtmlPageRest
 */
@Path( RestConstants.BASE_PATH + Constants.API_PATH + Constants.VERSION_PATH + Constants.HTMLPAGE_PATH )
public class HtmlPageRest
{
    private static final int VERSION_1 = 1;

    /**
     * Get HtmlPage
     * 
     * @param nVersion
     *            the API version
     * @param id
     *            the id
     * @param idDefault
     *            the idDefault
     * @return the HtmlPage
     */
    @GET
    @Path( Constants.ID_PATH )
    @Produces( MediaType.APPLICATION_JSON )
    public Response getHtmlPage( @Context HttpServletRequest request, @PathParam( Constants.VERSION ) Integer nVersion, @PathParam( Constants.ID ) int id, @QueryParam( Constants.ID_DEFAULT ) int idDefault )
    {
        ResponseBuilder responseBuilder = Response.ok();
        if ( nVersion == VERSION_1 )
        {            
            AllowCorsOriginUtil.setResponse( responseBuilder, request );         
            return getHtmlPageV1( id, idDefault );
        }
        AppLogService.error( Constants.ERROR_NOT_FOUND_VERSION );
        return responseBuilder.status( Response.Status.NOT_FOUND ).entity( JsonUtil.buildJsonResponse( new ErrorJsonResponse( Response.Status.NOT_FOUND.name( ), Constants.ERROR_NOT_FOUND_VERSION ) ) )
                .build( );
    }

    /**
     * Get HtmlPage V1
     * 
     * @param id
     *            the id
     * @param idDefault
     *            the idDefault
     * @return the HtmlPage for the version 1
     */
    private Response getHtmlPageV1( int id, int idDefault )
    {
        HtmlPage htmlPage = HtmlPageService.getInstance( ).getHtmlPageCache( id );
        
        if ( htmlPage == null || HtmlPageUtil.isRoleExist( htmlPage.getRole( ) ) )
        {
            htmlPage = HtmlPageService.getInstance( ).getHtmlPageCache( idDefault );
        }

        if ( htmlPage == null || HtmlPageUtil.isRoleExist( htmlPage.getRole( ) ) )
        {
            AppLogService.error( Constants.ERROR_NOT_FOUND_RESOURCE );
            return Response.status( Response.Status.NOT_FOUND ).entity( JsonUtil.buildJsonResponse( new ErrorJsonResponse( Response.Status.NOT_FOUND.name( ), Constants.ERROR_NOT_FOUND_RESOURCE ) ) )
                    .build( );
        }

        return Response.status( Response.Status.OK ).entity( JsonUtil.buildJsonResponse( new JsonResponse( htmlPage.getHtmlContent( ) ) ) ).build( );
    }

}
