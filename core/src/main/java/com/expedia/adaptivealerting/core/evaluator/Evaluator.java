/*
 * Copyright 2018-2019 Expedia Group, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.expedia.adaptivealerting.core.evaluator;

/**
 * Interface for components that evaluate model performance, using measures such as RMSE, MAPE, sMAPE, etc.
 */
public interface Evaluator {

    /**
     * Updates the internal state of the evaluator.
     *
     * @param observed  Observed value.
     * @param predicted Predicted value.
     */
    void update(double observed, double predicted);

    /**
     * Returns a score which tells how good the fit is. Exact score is implementation-dependent and some scores are
     * scale-dependent and some scale-independent.
     *
     * @return Model evaluation.
     */
    ModelEvaluation evaluate();

    /**
     * Resets the internal state of the evaluator.
     */
    void reset();
}
