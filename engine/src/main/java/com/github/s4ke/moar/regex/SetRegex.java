/*
 The MIT License (MIT)

 Copyright (c) 2016 Martin Braun

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */
package com.github.s4ke.moar.regex;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import com.github.s4ke.moar.moa.Moa;
import com.github.s4ke.moar.moa.edgegraph.EdgeGraph;
import com.github.s4ke.moar.moa.edgegraph.MemoryAction;
import com.github.s4ke.moar.moa.states.SetState;
import com.github.s4ke.moar.moa.states.State;
import com.github.s4ke.moar.moa.states.Variable;
import com.github.s4ke.moar.strings.CodePointSet;
import com.github.s4ke.moar.strings.EfficientString;

/**
 * @author Martin Braun
 */
class SetRegex implements Regex {

	private static final String SELF_RELEVANT_KEY = "";

	private final CodePointSet setDescriptor;
	private final String stringRepresentation;

	public SetRegex(CodePointSet setDescriptor, String stringRepresentation) {
		this.setDescriptor = setDescriptor;
		this.stringRepresentation = stringRepresentation;
	}

	@Override
	public Regex copy() {
		return new SetRegex( this.setDescriptor, this.stringRepresentation );
	}

	@Override
	public void contributeEdges(
			EdgeGraph edgeGraph,
			Map<String, Variable> variables,
			Set<State> states,
			Map<Regex, Map<String, State>> selfRelevant) {
		State state = selfRelevant.get( this ).get( SELF_RELEVANT_KEY );
		edgeGraph.addEdgeWithDeterminismCheck( Moa.SRC, new EdgeGraph.Edge( MemoryAction.NO_OP, state ), this );
		edgeGraph.addEdgeWithDeterminismCheck( state, new EdgeGraph.Edge( MemoryAction.NO_OP, Moa.SNK ), this );
	}

	@Override
	public void contributeStates(
			Map<String, Variable> variables,
			Set<State> states,
			Map<Regex, Map<String, State>> selfRelevant,
			Supplier<Integer> idxSupplier) {
		//we default to length 1
		State state = new SetState( idxSupplier.get(), 1, this.setDescriptor, stringRepresentation );

		states.add( state );
		states.add( Moa.SRC );
		states.add( Moa.SNK );
		selfRelevant.put( this, new HashMap<>() );
		selfRelevant.get( this ).put( SELF_RELEVANT_KEY, state );
	}

	@Override
	public void calculateVariableOccurences(
			Map<String, Variable> variables, Supplier<Integer> varIdxSupplier) {

	}

	@Override
	public String toString() {
		if ( this.stringRepresentation != null ) {
			return this.stringRepresentation;
		}
		else {
			return this.setDescriptor.toString();
		}
	}
}
