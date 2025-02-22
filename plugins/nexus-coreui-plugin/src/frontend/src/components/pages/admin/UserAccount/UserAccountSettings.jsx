/*
 * Sonatype Nexus (TM) Open Source Version
 * Copyright (c) 2008-present Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc. "Sonatype" and "Sonatype Nexus" are trademarks
 * of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation. M2eclipse is a trademark of the
 * Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
import React from 'react';
import {useService} from '@xstate/react';
import {
  Alert,
  FieldWrapper,
  Section,
  SectionFooter,
  Textfield,
  Utils
} from '@sonatype/nexus-ui-plugin';
import {
  NxButton,
  NxLoadWrapper,
  NxSubmitMask,
  NxTooltip,
} from '@sonatype/react-shared-components';
import UIStrings from '../../../../constants/UIStrings';

export default function UserAccountSettings({service}) {
  const [current, send] = useService(service);
  const context = current.context;
  const data = context.data;
  const external = data?.external;
  const isLoading = current.matches('loading');
  const isSaving = current.matches('saving');
  const isPristine = context.isPristine;
  const validationErrors = context.validationErrors;
  const isInvalid = Utils.isInvalid(validationErrors);
  const hasData = data && data !== {};

  function handleSave(evt) {
    evt.preventDefault();
    send('SAVE');
  }

  function handleDiscard() {
    send('RESET');
  }

  function handleChange({target}) {
    send('UPDATE', {
      data: {
        [target.name]: target.value
      }
    });
  }

  function retry() {
    send('RETRY');
  }

  let error = null;
  if (context.error instanceof Array) {
    error = (
        <Alert type="error">
          {UIStrings.USER_ACCOUNT.MESSAGES.UPDATE_ERROR}
          <ul>
            {context.error.map(e => <li key={e.id}>{JSON.stringify(e)}</li>)}
          </ul>
        </Alert>
    );
  }
  else if (context.error) {
    error = (
        <Alert type="error">
          {UIStrings.USER_ACCOUNT.MESSAGES.UPDATE_ERROR}<br/>
          {context.error}
        </Alert>
    );
  }

  return <Section>
    <NxLoadWrapper loading={isLoading} retryHandler={retry}>
      {hasData && <>
        {isSaving && <NxSubmitMask message={UIStrings.SAVING}/>}
        {error}

        <FieldWrapper labelText={UIStrings.USER_ACCOUNT.ID_FIELD_LABEL}
                      id="id-group">
          <Textfield name="userId" readOnly disabled value={data.userId}/>
        </FieldWrapper>
        <FieldWrapper labelText={UIStrings.USER_ACCOUNT.FIRST_FIELD_LABEL}
                      id="first-name-group">
          <Textfield {...buildFieldProps('firstName', current, handleChange)}/>
        </FieldWrapper>
        <FieldWrapper labelText={UIStrings.USER_ACCOUNT.LAST_FIELD_LABEL}
                      id='last-name-group'>
          <Textfield {...buildFieldProps('lastName', current, handleChange)}/>
        </FieldWrapper>
        <FieldWrapper labelText={UIStrings.USER_ACCOUNT.EMAIL_FIELD_LABEL}
                      id='email-group'>
          <Textfield {...buildFieldProps('email', current, handleChange)}/>
        </FieldWrapper>
        <SectionFooter>
          <NxTooltip title={Utils.saveTooltip({isPristine, isInvalid})}>
            <NxButton variant='primary'
                      className={(isPristine || isInvalid) && 'disabled'}
                      disabled={external}
                      onClick={handleSave}
                      id='useraccount-save-button'>
              {UIStrings.SETTINGS.SAVE_BUTTON_LABEL}
            </NxButton>
          </NxTooltip>
          <NxTooltip title={Utils.discardTooltip({isPristine})}>
            <NxButton disabled={external}
                      className={isPristine && 'disabled'}
                      onClick={handleDiscard}
                      id='useraccount-discard-button'>
              {UIStrings.SETTINGS.DISCARD_BUTTON_LABEL}
            </NxButton>
          </NxTooltip>
        </SectionFooter>
      </>}
    </NxLoadWrapper>
  </Section>;
}

function buildFieldProps(name, current, handleChange) {
  const readOnly = current.context.data?.external;
  return {
    ...Utils.fieldProps(name, current),
    disabled: readOnly,
    readOnly,
    onChange: handleChange
  }
}
