/*
 * Copyright (c) 2002-2015 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.cypher.internal.compiler.v2_3.codegen;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.neo4j.graphdb.Direction;
import org.neo4j.helpers.collection.IteratorUtil;
import org.neo4j.kernel.api.ReadOperations;
import org.neo4j.kernel.api.exceptions.EntityNotFoundException;
import org.neo4j.kernel.impl.api.RelationshipVisitor;
import org.neo4j.kernel.impl.api.store.RelationshipIterator;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.neo4j.cypher.internal.compiler.v2_3.codegen.CompiledExpandUtils.connectingRelationships;

public class CompiledExpandUtilsTest
{
    @Test
    public void shouldUseGivenOrderIfItHasLowerDegree() throws EntityNotFoundException
    {
        // GIVEN
        ReadOperations readOperations = mock( ReadOperations.class );
        when( readOperations.nodeGetDegree( 1L, Direction.OUTGOING ) ).thenReturn( 1 );
        when( readOperations.nodeGetDegree( 2L, Direction.INCOMING ) ).thenReturn( 3 );

        // WHEN
       connectingRelationships( readOperations, 1L, Direction.OUTGOING, 2L );

        // THEN
        verify( readOperations, times( 1 ) ).nodeGetRelationships( 1L, Direction.OUTGOING );
    }

    @Test
    public void shouldSwitchOrderIfItHasLowerDegree() throws EntityNotFoundException
    {
        // GIVEN
        ReadOperations readOperations = mock( ReadOperations.class );
        when( readOperations.nodeGetDegree( 1L, Direction.OUTGOING ) ).thenReturn( 3 );
        when( readOperations.nodeGetDegree( 2L, Direction.INCOMING ) ).thenReturn( 1 );

        // WHEN
        connectingRelationships( readOperations, 1L, Direction.OUTGOING, 2L );

        // THEN
        verify( readOperations, times( 1 ) ).nodeGetRelationships( 2L, Direction.INCOMING );
    }

    @Test
    public void shouldUseGivenOrderIfItHasLowerDegreeWithTypes() throws EntityNotFoundException
    {
        // GIVEN
        ReadOperations readOperations = mock( ReadOperations.class );
        when( readOperations.nodeGetDegree( 1L, Direction.OUTGOING, 1 ) ).thenReturn( 1 );
        when( readOperations.nodeGetDegree( 2L, Direction.INCOMING, 1 ) ).thenReturn( 3 );

        // WHEN
        connectingRelationships( readOperations, 1L, Direction.OUTGOING, 2L, 1 );

        // THEN
        verify( readOperations, times( 1 ) ).nodeGetRelationships( 1L, Direction.OUTGOING, 1 );
    }

    @Test
    public void shouldSwitchOrderIfItHasLowerDegreeWithTypes() throws EntityNotFoundException
    {
        // GIVEN
        ReadOperations readOperations = mock( ReadOperations.class );
        when( readOperations.nodeGetDegree( 1L, Direction.OUTGOING, 1 ) ).thenReturn( 3 );
        when( readOperations.nodeGetDegree( 2L, Direction.INCOMING, 1 ) ).thenReturn( 1 );

        // WHEN
        connectingRelationships( readOperations, 1L, Direction.OUTGOING, 2L, 1 );

        // THEN
        verify( readOperations, times( 1 ) ).nodeGetRelationships( 2L, Direction.INCOMING, 1 );
    }

}
