/*
 * net/balusc/jsf/component/html/HtmlInputFile.java
 *
 * Copyright (C) 2009 BalusC
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package pl.softwaremill.common.faces.fileupload;

import javax.faces.component.FacesComponent;
import javax.faces.component.html.HtmlInputText;

/**
 * Faces component for <code>input type="file"</code> field.
 *
 * @author BalusC
 * @link http://balusc.blogspot.com/2009/12/uploading-files-with-jsf-20-and-servlet.html
 */
@FacesComponent("FileUpload")
public class FileUpload extends HtmlInputText {

    @Override
    public String getRendererType() {
        return "javax.faces.File";
    }

}
