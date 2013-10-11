/*
 * Copyright 2002-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.devnexus.ting.core.dao.jpa;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.devnexus.ting.core.dao.DocumentDao;
import com.devnexus.ting.core.model.FileData;

@Repository("documentDao")
public class DocumentDaoJpa extends GenericDaoJpa< FileData, Long>
						   implements DocumentDao {

	/** Constructor. */
	private DocumentDaoJpa() {
		super(FileData.class);
	}

	@Override
	public List<FileData> getAllDocuments() {
		return super.entityManager
		.createQuery("select doc from FileData doc "
				   + "order by doc.name ASC", FileData.class)
		.getResultList();
	}

}