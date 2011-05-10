/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron, Member of the Helmholtz
 * Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. WITHOUT WARRANTY OF ANY
 * KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN ANY RESPECT, THE USER ASSUMES
 * THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY
 * CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER
 * EXCEPT UNDER THIS DISCLAIMER. DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
 * ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION,
 * MODIFICATION, USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY AT
 * HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.sds.behavior.desy;

import org.csstudio.sds.components.model.TextInputModel;
import org.csstudio.sds.cursorservice.CursorService;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.BorderStyleEnum;
import org.csstudio.sds.model.TextTypeEnum;
import org.epics.css.dal.simple.AnyData;
import org.epics.css.dal.simple.MetaData;

/**
 *
 * Default DESY-Behavior for the {@link TextInputModel} widget with Connection state
 *
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.8 $
 * @since 20.04.2010
 */
public class TextinputConnectionBehavior extends AbstractDesyConnectionBehavior<TextInputModel> {
    
    /**
     * Constructor.
     */
    public TextinputConnectionBehavior() {
        addInvisiblePropertyId(TextInputModel.PROP_INPUT_TEXT);
        addInvisiblePropertyId(TextInputModel.PROP_ACTIONDATA);
        addInvisiblePropertyId(TextInputModel.PROP_PERMISSSION_ID);
        addInvisiblePropertyId(TextInputModel.PROP_BORDER_WIDTH);
        addInvisiblePropertyId(TextInputModel.PROP_BORDER_STYLE);
        addInvisiblePropertyId(TextInputModel.PROP_BORDER_COLOR);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void doInitialize(final TextInputModel widget) {
        super.doInitialize(widget);
        widget.setPropertyValue(TextInputModel.PROP_BORDER_STYLE,
                                BorderStyleEnum.LOWERED.getIndex());
        if (widget.getValueType().equals(TextTypeEnum.TEXT)) {
            widget.setJavaType(String.class);
        }
        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void doProcessValueChange(final TextInputModel model, final AnyData anyData) {
        super.doProcessValueChange(model, anyData);
        // .. fill level (influenced by current value)
        model.setPropertyValue(TextInputModel.PROP_INPUT_TEXT, anyData.stringValue());
        System.out.println("getPrecision2: " + anyData.getMetaData().getPrecision());
    }
    
    @Override
    protected void doProcessMetaDataChange(final TextInputModel widget, final MetaData metaData) {
        if (metaData != null) {
            switch (metaData.getAccessType()) {
                case NONE:
                    widget.setPropertyValue(AbstractWidgetModel.PROP_CURSOR, CursorService
                            .getInstance().availableCursors().get(7));
                    break;
                case READ:
                    widget.setPropertyValue(AbstractWidgetModel.PROP_CURSOR, CursorService
                            .getInstance().availableCursors().get(7));
                    break;
                case READ_WRITE:
                    widget.setPropertyValue(AbstractWidgetModel.PROP_CURSOR, CursorService
                            .getInstance().availableCursors().get(0));
                    break;
                case WRITE:
                    widget.setPropertyValue(AbstractWidgetModel.PROP_CURSOR, CursorService
                            .getInstance().availableCursors().get(0));
                    break;
                default:
                    widget.setPropertyValue(AbstractWidgetModel.PROP_CURSOR, CursorService
                            .getInstance().availableCursors().get(0));
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected String[] doGetSettablePropertyIds() {
        return new String[] {TextInputModel.PROP_INPUT_TEXT};
    }
}
